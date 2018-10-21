package br.com.rar.soldi.shopline.controller.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StatusPagamentoEventoResponse implements Serializable {

	private static final long serialVersionUID = 3544254076111851932L;

	private boolean erro = false;
	
	private String mensagemErro;
	
	private List<DadosStatusPagamentoEventoRetorno> dados = new ArrayList<DadosStatusPagamentoEventoRetorno>();
	
	public boolean isErro() {
		return erro;
	}

	public void setErro(boolean erro) {
		this.erro = erro;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

	public void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	public List<DadosStatusPagamentoEventoRetorno> getDados() {
		return dados;
	}

	public void setDados(List<DadosStatusPagamentoEventoRetorno> dados) {
		this.dados = dados;
	}
	
}
