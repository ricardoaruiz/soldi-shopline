package br.com.rar.soldi.shopline.integration.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Inscricao implements Serializable{

	private static final long serialVersionUID = -4768491975634041700L;
	
	private String referencia;
	private int tipo;
	private Pessoa pessoa;
	private Evento evento;
	private Pagamento pagamento;
	private List<Inscrito> inscritos;

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
	public List<Inscrito> getInscritos() {
		return inscritos;
	}
	public void setInscritos(List<Inscrito> inscritos) {
		this.inscritos = inscritos;
	}
	@Override
	public String toString() {
		return "Inscricao [referencia=" + referencia + ", tipo=" + tipo + "]";
	}
	
}
