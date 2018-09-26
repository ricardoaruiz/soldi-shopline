package br.com.rar.soldi.shopline.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;

import br.com.rar.soldi.shopline.Itaucripto;
import br.com.rar.soldi.shopline.controller.model.DadosStatusPagamentoEvento;
import br.com.rar.soldi.shopline.controller.model.DadosStatusPagamentoEventoRetorno;
import br.com.rar.soldi.shopline.controller.model.StatusPagamentoEventoRequest;
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
	
	private static final String CODIGO_EMPRESA_AESAS = "1";
	private static final String CODIGO_EMPRESA_SOLDI = "2";
	
	/**
	 * Formato a ser retornado na consulta de status de pagamento
	 */
	private static final String FORMATO_CONSULTA_XML = "1";
	
	/**
	 * Apis do sistema Soldi
	 */
	@Autowired
	private ApiSoldi apiSoldi;
	
	/**
	 * Api para consulta de status de pagamento no shopline
	 */
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
	
	/**
	 * Obtem os dados a serem enviados para o shopline na emissão do boleto.
	 * @param referencia
	 * @return
	 * @throws ServiceException
	 * @throws InscricaoNaoEncontradaException
	 * @throws DadosObrigatoriosPagamentoException
	 */
	public String obtemDadosPagamento(String referencia) throws ServiceException, InscricaoNaoEncontradaException, DadosObrigatoriosPagamentoException {
		Inscricao inscricao = obtemInscricao(referencia);
		return getDadosPagamento(inscricao);
	}
	
	/**
	 * Faz a consulta ao serviço de status de pagamento no shopline para cada inscrição informada
	 * @param dadosStatusPagamento
	 * @return
	 * @throws ServiceException
	 */
	public List<DadosStatusPagamentoEventoRetorno> obtemDadosConsultaStatus(
			StatusPagamentoEventoRequest dadosStatusPagamento) throws ServiceException {
		
		try {
			
			List<DadosStatusPagamentoEventoRetorno> retorno = new ArrayList<DadosStatusPagamentoEventoRetorno>();
			
			for(DadosStatusPagamentoEvento dados : dadosStatusPagamento.getDados()) {

				// Monta requisição
				String dadosConsultaStatusPagamento = getDadosConsultaStatusPagamento(dados);
				
				// Faz a chamada ao serviço de consulta
				Call<String> call = apiConsultaStatusShopline.getStatusPagamento().consultaStatus(dadosConsultaStatusPagamento);
				Response<String> execute = call.execute();
				
				// Faz o parse do retorno do serviço
				StatusPagamento statusPagamento = getStatusPagamento(execute.body().toString());
				
				// Adiciona cada retorno do serviço na lista para ser devolvida ao controller
				retorno.add(getDadosRetornoConsultaStatus(dados, statusPagamento));				
			}
			
			return retorno;
			
		} catch (IOException e) {
			throw new ServiceException("Erro na chamada ao serviço de consulta de status de pagamento do shopline", e);
		} catch (DadosObrigatoriosPagamentoException e) {
			throw new ServiceException("Dados obrigatórios para a consulta de pagamento não foram informados", e);
		}
	}
	
	/**
	 * Monta cada registro obtido do serviço de consulta de status de pagamento
	 * @param dados
	 * @param statusPagamento
	 * @return
	 */
	private DadosStatusPagamentoEventoRetorno getDadosRetornoConsultaStatus(
			DadosStatusPagamentoEvento dados, StatusPagamento statusPagamento) {
		
		DadosStatusPagamentoEventoRetorno dadosRetorno = new DadosStatusPagamentoEventoRetorno(
				dados.getReferencia(), dados.getPedido());
		
		dadosRetorno.setCodAut(statusPagamento.getCodAut());
		dadosRetorno.setCodEmp(statusPagamento.getCodEmp());
		dadosRetorno.setCompVend(statusPagamento.getCompVend());
		dadosRetorno.setDtPag(statusPagamento.getDtPag());
		dadosRetorno.setNumId(statusPagamento.getNumId());
		dadosRetorno.setSitPag(statusPagamento.getSitPag());
		dadosRetorno.setTipCart(statusPagamento.getTipCart());
		dadosRetorno.setTipPag(statusPagamento.getTipPag());
		dadosRetorno.setValor(statusPagamento.getValor());
		dadosRetorno.setValorPago(statusPagamento.getValorPago());
		
		return dadosRetorno;
	}
	
	/**
	 * Busca no sistema Soldi a inscrição através de sua referencia
	 * @param referencia
	 * @return
	 * @throws ServiceException
	 * @throws InscricaoNaoEncontradaException
	 */
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
			
	/**
	 * Monta a string que será passada na emissão de boleto para o shopline
	 * @param inscricao
	 * @return
	 * @throws DadosObrigatoriosPagamentoException
	 */
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

	/**
	 * Monta a string que será passada para o serviço de consulta de status de pagamento do shopline
	 * @param dados
	 * @return
	 * @throws DadosObrigatoriosPagamentoException
	 */
	private String getDadosConsultaStatusPagamento(DadosStatusPagamentoEvento dados) throws DadosObrigatoriosPagamentoException {
		String codigoEmpresa = getCodigoEmpresa(dados);
		String pedido = dados.getPedido();
		String formato = FORMATO_CONSULTA_XML;
		String codigoCriptografia = getCodigoCriptografia(dados);
		
		return new Itaucripto().geraConsulta(codigoEmpresa, pedido, formato, codigoCriptografia);				
	}
	
	/**
	 * Faz o parse do retorno do serviço de consulta de status de pagamento
	 * @param body
	 * @return
	 * @throws ServiceException
	 */
	private StatusPagamento getStatusPagamento(String body) throws ServiceException {
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
			throw new ServiceException("Erro ao converter o retorno da consulta em Json", e);
		}
	}
	
	/**
	 * Valida os dados obrigatórios para a emissão do boleto
	 * @param codigoEmpresa
	 * @param pedido
	 * @param valorAPagar
	 * @param codigoCriptografia
	 * @throws DadosObrigatoriosPagamentoException
	 */
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
		case CODIGO_EMPRESA_AESAS:
			return this.codigoEmpresaAesas;
		case CODIGO_EMPRESA_SOLDI:
			return this.codigoEmpresaSoldi; 
		default:
			throw new DadosObrigatoriosPagamentoException("Dados obrigatórios para o pagamento (empresa) não foram informados. Entre em contato com a organização do evento");
		} 
	}
	
	private String getCodigoEmpresa(DadosStatusPagamentoEvento dados) throws DadosObrigatoriosPagamentoException {
		switch (dados.getOrganizador()) {
		case CODIGO_EMPRESA_AESAS:
			return this.codigoEmpresaAesas;
		case CODIGO_EMPRESA_SOLDI:
			return this.codigoEmpresaSoldi; 
		default:
			throw new DadosObrigatoriosPagamentoException("Dados obrigatórios para o pagamento (empresa) não foram informados. Entre em contato com a organização do evento");
		} 
	}	
	
	private String getCodigoCriptografia(Inscricao inscricao) throws DadosObrigatoriosPagamentoException {
		switch (inscricao.getEvento().getOrganizador()) {
		case CODIGO_EMPRESA_AESAS:
			return this.codigoCriptografiaAesas;
		case CODIGO_EMPRESA_SOLDI:
			return this.codigoCriptografiaSoldi; 
		default:
			throw new DadosObrigatoriosPagamentoException("Dados obrigatórios para o pagamento (cripto) não foram informados. Entre em contato com a organização do evento");
		} 
	}
	
	private String getCodigoCriptografia(DadosStatusPagamentoEvento dados) throws DadosObrigatoriosPagamentoException {
		switch (dados.getOrganizador()) {
		case CODIGO_EMPRESA_AESAS:
			return this.codigoCriptografiaAesas;
		case CODIGO_EMPRESA_SOLDI:
			return this.codigoCriptografiaSoldi; 
		default:
			throw new DadosObrigatoriosPagamentoException("Dados obrigatórios para o pagamento (cripto) não foram informados. Entre em contato com a organização do evento");
		} 
	}
	
}
