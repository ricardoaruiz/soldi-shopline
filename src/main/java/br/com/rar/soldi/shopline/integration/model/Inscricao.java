package br.com.rar.soldi.shopline.integration.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Inscricao implements Serializable{

	private static final long serialVersionUID = 7425284843327647201L;
	
	private String referencia;
	private int tipo;
	private Pessoa pessoa;	
	private Evento evento;	
	private Pagamento pagamento;	

	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}		
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public Evento getEvento() {
		return evento;
	}
	public void setEvento(Evento evento) {
		this.evento = evento;
	}
	
	public Pagamento getPagamento() {
		return pagamento;
	}
	public void setPagamento(Pagamento pagamento) {
		this.pagamento = pagamento;
	}
	@Override
	public String toString() {
		return "Inscricao [referencia=" + referencia + ", tipo=" + tipo + "]";
	}
	
}
