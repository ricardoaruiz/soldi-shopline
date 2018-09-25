package br.com.rar.soldi.shopline.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import br.com.rar.soldi.shopline.Itaucripto;
import br.com.rar.soldi.shopline.integration.ApiConsultaStatusShopline;
import br.com.rar.soldi.shopline.integration.ApiSoldi;
import br.com.rar.soldi.shopline.integration.model.Inscricao;
import br.com.rar.soldi.shopline.integration.model.StatusPagamento;
import br.com.rar.soldi.shopline.service.exception.DadosObrigatoriosPagamentoException;
import br.com.rar.soldi.shopline.service.exception.InscricaoNaoEncontradaException;
import br.com.rar.soldi.shopline.service.exception.ServiceException;
import retrofit2.Call;
import retrofit2.Response;

@Service
public class PagamentoService {

	private static final String PESSOA_FISICA = "01";
	private static final String PESSOA_JURIDICA = "02";
	private static final String FORMATO_CONSULTA_XML = "1";
	
	@Autowired
	private ApiSoldi apiSoldi;
	
	@Autowired
	private ApiConsultaStatusShopline apiConsultaStatusShopline;
	
	@Value("${codigoEmpresaSoldi}")
	private String codigoEmpresaSoldi;
	
	@Value("${codigoCriptografiaSoldi}")
	private String codigoCriptografiaSoldi;
	
	@Value("${codigoEmpresaAesas}")
	private String codigoEmpresaAesas;
	
	@Value("${codigoCriptografiaAesas}")
	private String codigoCriptografiaAesas;	
	
	@Value("${numeroDiasVencimentoBoleto}")
	private int numeroDiasVencimentoBoleto;	
	
	public String obtemDadosPagamento(String referencia) throws ServiceException, InscricaoNaoEncontradaException, DadosObrigatoriosPagamentoException {
		Inscricao inscricao = obtemInscricao(referencia);
		return getDadosPagamento(inscricao);
	}
	
	// TODO aqui deve receber o id do evento e buscar todas as incrições novas do evento
	public void obtemDadosConsultaStatus(String referencia) throws ServiceException, InscricaoNaoEncontradaException, DadosObrigatoriosPagamentoException {
		
		//Receber o id do evento e chamar um serviço na Soldi-api que retorne todas as inscrições novas

		//Para cada inscrição retornada chamar o serviço de consulta do shopline e guargar o retorno em um map 
		//onde a chave é a referencia da inscrição
		
		//No final pegar o map, montar uma request passando um json com um par de chave valor (referencia, status)
		//para um serviço no Soldi-api para atualizar o status das inscrições
		
		try {
			Inscricao inscricao = obtemInscricao(referencia);
			String dadosConsultaStatusPagamento = getDadosConsultaStatusPagamento(inscricao);
			Call<String> call = apiConsultaStatusShopline.getStatusPagamento().consultaStatus(dadosConsultaStatusPagamento);
			Response<String> execute = call.execute();
			
			StatusPagamento statusPagamento = getStatusPagamento(execute.body().toString());			
			System.out.println(statusPagamento);	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Inscricao obtemInscricao(String referencia) throws ServiceException, InscricaoNaoEncontradaException {		
		try {
			Call<Inscricao> call = apiSoldi.getInscricaoService().buscarInscricao(referencia);
			Response<Inscricao> execute = call.execute();
			
			if(execute.isSuccessful()) {
				Inscricao inscricao = execute.body();				
				return inscricao;				
			} else {
				throw new InscricaoNaoEncontradaException("Inscrição não encontrada. Entre em contato com a organização do evento.");				
			}			
			
		} catch (IOException e) {
			System.out.println(e);
			throw new ServiceException("Ocorreu um erro ao processar o seu pagamento. Entre em contato com a organização do evento.", e);
		}		
	}
			
	private String getDadosPagamento(Inscricao inscricao) throws DadosObrigatoriosPagamentoException {
		
		String codigoEmpresa = getCodigoEmpresa(inscricao);
		String codigoCriptografia = getCodigoCriptografia(inscricao);
		String pedido = getPedido(inscricao);
		String valorAPagar = getValorAPagar(inscricao);
		String observacao = getObservacao(inscricao);
		String nomeSacado = getNomeSacado(inscricao);		
		String codigoInscricao = getCodigoInscricao(inscricao);
		String numeroInscricao = getNumeroInscricao(inscricao);				
		String endereco = getEndereco(inscricao);
		String bairro = getBairroSacado(inscricao);
		String cep = getCepSacado(inscricao);
		String cidade = getCidadeSacado(inscricao);
		String estado = getEstadoSacado(inscricao);
		
		validaDadosObrigatorios(codigoEmpresa, pedido, valorAPagar, codigoCriptografia);			
		
		Itaucripto cripto = new Itaucripto();
		return cripto.geraDados(codigoEmpresa, 
						 pedido, 
						 valorAPagar, 
						 observacao,
						 codigoCriptografia, 
						 nomeSacado, 
						 codigoInscricao, 
						 numeroInscricao, 
						 endereco, 
						 bairro, 
						 cep, 
						 cidade, 
						 estado, 
						 "", //Data Vencimento 
						 "URL de retorno", //TODO externalizar em variável de ambiente e no application.properties
						 "", 
						 "", 
						 "");
	}

	private String getDadosConsultaStatusPagamento(Inscricao inscricao) throws DadosObrigatoriosPagamentoException {		
		String codigoEmpresa = getCodigoEmpresa(inscricao);
		String pedido = getPedido(inscricao);
		String formato = FORMATO_CONSULTA_XML;
		String codigoCriptografia = getCodigoCriptografia(inscricao);
		
		return new Itaucripto().geraConsulta(codigoEmpresa, pedido, formato, codigoCriptografia);				
	}
	
	private StatusPagamento getStatusPagamento(String body) {
		try {
			body = body.substring(body.indexOf("<PARAMETER>")+11);
			body = body.substring(0, body.length()-11);
			body = body.replace("</PARAMETER>", "");
			body = body.replace("<PARAM ID=\"", "\"");
			body = body.replace("\" VALUE=\"", "\" : \"");
			body = body.replace("\"/>", "\",");			
			body = "{" + body.substring(0, body.length()-3) + "}";
			
			Gson g = new Gson();
			StatusPagamento statusPagamento = g.fromJson(body, StatusPagamento.class);
			return statusPagamento;
		} catch(Exception e) {
			throw new IllegalStateException("Erro ao converter o retorno da consulta em Json", e);
		}
	}
	
	private void validaDadosObrigatorios(String codigoEmpresa, String pedido, String valorAPagar, String codigoCriptografia) throws DadosObrigatoriosPagamentoException {		
		if (StringUtils.isEmpty(codigoEmpresa) || StringUtils.isEmpty(pedido) || 
				StringUtils.isEmpty(valorAPagar) || StringUtils.isEmpty(codigoCriptografia)) {
			throw new DadosObrigatoriosPagamentoException("Dados obrigatórios para o pagamento não foram informados. Entre em contato com a organização do evento");
		}
	}

	private String getPedido(Inscricao inscricao) {		
		return inscricao.getPagamento() == null ? "" : Integer.toString(inscricao.getPagamento().getId());
	}
	
	private String getValorAPagar(Inscricao inscricao) {
		return  inscricao.getEvento().getValorAPagar();
	}
	
	private String getObservacao(Inscricao inscricao) {
		StringBuilder obs = new StringBuilder();
		obs.append(inscricao.getEvento().getTitulo());
		if(inscricao.getEvento().getDataInicio() != null) {
			obs.append(" - de ").append(inscricao.getEvento().getDataInicio());
		}
		if(inscricao.getEvento().getDataFim() != null) {
			obs.append(" até ").append(inscricao.getEvento().getDataFim());
		}		
		return obs.toString();
	}
	
	private String getCodigoInscricao(Inscricao inscricao) {
		if(inscricao.getPessoa() == null) {
			return "";
		}
		return inscricao.getPessoa().getTipo() == 1 ? PESSOA_FISICA : PESSOA_JURIDICA;
	}
	
	private String getNumeroInscricao(Inscricao inscricao) {
		if(inscricao.getPessoa() == null) {
			return "";
		}
		return getCodigoInscricao(inscricao).equals(PESSOA_FISICA) ? inscricao.getPessoa().getCpf() : inscricao.getPessoa().getCnpj();
	}
	
	private String getNomeSacado(Inscricao inscricao) {
		if(inscricao.getPessoa() == null) {
			return "";
		}
		return getCodigoInscricao(inscricao).equals(PESSOA_FISICA) ? inscricao.getPessoa().getNome() : inscricao.getPessoa().getRazaoSocial();
	}
	
	private String getEndereco(Inscricao inscricao) {
		if(inscricao.getPessoa() == null || inscricao.getPessoa().getEndereco() == null) {
			return "";
		}
		return inscricao.getPessoa().getEndereco().getRua() + " " + inscricao.getPessoa().getEndereco().getNumero();
	}	

	private String getEstadoSacado(Inscricao inscricao) {
		return inscricao.getPessoa() == null || inscricao.getPessoa().getEndereco() == null ? "" : inscricao.getPessoa().getEndereco().getEstado();
	}

	private String getCidadeSacado(Inscricao inscricao) {
		return inscricao.getPessoa() == null || inscricao.getPessoa().getEndereco() == null ? "" : inscricao.getPessoa().getEndereco().getCidade();
	}

	private String getCepSacado(Inscricao inscricao) {
		return inscricao.getPessoa() == null || inscricao.getPessoa().getEndereco() == null ? "" : inscricao.getPessoa().getEndereco().getCep();
	}

	private String getBairroSacado(Inscricao inscricao) {		
		return inscricao.getPessoa() == null || inscricao.getPessoa().getEndereco() == null ? "" : inscricao.getPessoa().getEndereco().getBairro();
	}
	
	private String getCodigoEmpresa(Inscricao inscricao) throws DadosObrigatoriosPagamentoException {
		switch (inscricao.getEvento().getOrganizador()) {
		case "1":
			return this.codigoEmpresaAesas;
		case "2":
			return this.codigoEmpresaSoldi; 
		default:
			throw new DadosObrigatoriosPagamentoException("Dados obrigatórios para o pagamento (empresa) não foram informados. Entre em contato com a organização do evento");
		} 
	}
	
	private String getCodigoCriptografia(Inscricao inscricao) throws DadosObrigatoriosPagamentoException {
		switch (inscricao.getEvento().getOrganizador()) {
		case "1":
			return this.codigoCriptografiaAesas;
		case "2":
			return this.codigoCriptografiaSoldi; 
		default:
			throw new DadosObrigatoriosPagamentoException("Dados obrigatórios para o pagamento (cripto) não foram informados. Entre em contato com a organização do evento");
		} 
	}	
	
}
