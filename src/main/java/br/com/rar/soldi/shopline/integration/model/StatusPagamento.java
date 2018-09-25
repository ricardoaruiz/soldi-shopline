package br.com.rar.soldi.shopline.integration.model;

import java.io.Serializable;

public class StatusPagamento implements Serializable{

	private static final long serialVersionUID = -2248998511944468024L;
	
	private String CodEmp;
	private String Pedido;
	private String Valor;
	private String tipPag;
	private String sitPag;
	private String ValorPago;
	private String dtPag;
	private String codAut;
	private String numId;
	private String compVend;
	private String tipCart;
	
	public String getCodEmp() {
		return CodEmp;
	}
	public void setCodEmp(String codEmp) {
		CodEmp = codEmp;
	}
	public String getPedido() {
		return Pedido;
	}
	public void setPedido(String pedido) {
		Pedido = pedido;
	}
	public String getValor() {
		return Valor;
	}
	public void setValor(String valor) {
		Valor = valor;
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
		return ValorPago;
	}
	public void setValorPago(String valorPago) {
		ValorPago = valorPago;
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
	
	@Override
	public String toString() {
		return "StatusPagamento [CodEmp=" + CodEmp + ", Pedido=" + Pedido + ", Valor=" + Valor + ", tipPag=" + tipPag
				+ ", sitPag=" + sitPag + ", ValorPago=" + ValorPago + ", dtPag=" + dtPag + ", codAut=" + codAut
				+ ", numId=" + numId + ", compVend=" + compVend + ", tipCart=" + tipCart + "]";
	}	
	
}
