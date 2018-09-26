package br.com.rar.soldi.shopline.controller.model;

import java.io.Serializable;

public class DadosStatusPagamentoEventoRetorno implements Serializable {

	private static final long serialVersionUID = 771577960252396376L;

	private String referencia;		
	private String codEmp;
	private String pedido;
	private String valor;
	private String tipPag;
	private String sitPag;
	private String valorPago;
	private String dtPag;
	private String codAut;
	private String numId;
	private String compVend;
	private String tipCart;
	
	public DadosStatusPagamentoEventoRetorno(String referencia, String pedido) {
		super();
		this.referencia = referencia;
		this.pedido = pedido;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getCodEmp() {
		return codEmp;
	}

	public void setCodEmp(String codEmp) {
		this.codEmp = codEmp;
	}

	public String getPedido() {
		return pedido;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getTipPag() {
		return tipPag;
	}

	public void setTipPag(String tipPag) {
		this.tipPag = tipPag;
	}

	public String getSitPag() {
		return sitPag;
	}

	public void setSitPag(String sitPag) {
		this.sitPag = sitPag;
	}

	public String getValorPago() {
		return valorPago;
	}

	public void setValorPago(String valorPago) {
		this.valorPago = valorPago;
	}

	public String getDtPag() {
		return dtPag;
	}

	public void setDtPag(String dtPag) {
		this.dtPag = dtPag;
	}

	public String getCodAut() {
		return codAut;
	}

	public void setCodAut(String codAut) {
		this.codAut = codAut;
	}

	public String getNumId() {
		return numId;
	}

	public void setNumId(String numId) {
		this.numId = numId;
	}

	public String getCompVend() {
		return compVend;
	}

	public void setCompVend(String compVend) {
		this.compVend = compVend;
	}

	public String getTipCart() {
		return tipCart;
	}

	public void setTipCart(String tipCart) {
		this.tipCart = tipCart;
	}
	
}
