package br.com.rar.soldi.shopline.controller.model;

import java.io.Serializable;

public class DadosStatusPagamentoEvento implements Serializable {

	private static final long serialVersionUID = 4551264189604708000L;

	private String referencia;
	
	private String pedido;
	
	private String organizador;

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getPedido() {
		return pedido;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

	public String getOrganizador() {
		return organizador;
	}

	public void setOrganizador(String organizador) {
		this.organizador = organizador;
	}
	
}
