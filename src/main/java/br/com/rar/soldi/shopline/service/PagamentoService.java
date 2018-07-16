package br.com.rar.soldi.shopline.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.rar.soldi.shopline.Itaucripto;
import br.com.rar.soldi.shopline.integration.ApiSoldi;
import br.com.rar.soldi.shopline.integration.model.Inscricao;
import br.com.rar.soldi.shopline.service.exception.DadosObrigatoriosPagamentoException;
import br.com.rar.soldi.shopline.service.exception.InscricaoNaoEncontradaException;
import br.com.rar.soldi.shopline.service.exception.ServiceException;
import retrofit2.Call;
import retrofit2.Response;

@Service
public class PagamentoService {

	private static final String PESSOA_FISICA = "01";
	private static final String PESSOA_JURIDICA = "02";
	
	@Autowired
	private ApiSoldi apiSoldi;
	
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
			throw new ServiceException("Ocorreu um erro ao processar o seu pagamento. Entre em contato com a organização do evento.", e);
		}		
	}
			
	private String getDadosPagamento(Inscricao inscricao) throws DadosObrigatoriosPagamentoException {
		
		String codigoEmpresa = getCodigoEmpresa(inscricao);
		String codigoCriptografia = getCodigoCriptografia(inscricao);
		String pedido = getPedido(inscricao);
		String valorAPagar = getValorAPagar(inscricao);
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
						 "", //Observacao
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
