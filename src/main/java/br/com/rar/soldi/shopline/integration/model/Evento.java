package br.com.rar.soldi.shopline.integration.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Evento implements Serializable{

	private static final long serialVersionUID = 5099894425401257038L;
	
	private int id;
	private String titulo;
	private String descricao;
	private String identificador;
	@JsonProperty("dt_inicio")
	private String dataInicio;
	@JsonProperty("dt_fim")
	private String dataFim;
	private String preco;
	private String precoTotal;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}
	public String getDataFim() {
		return dataFim;
	}
	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}
	public String getPreco() {
		return preco;
	}
	public void setPreco(String preco) {
		this.preco = preco;
	}
	public String getPrecoTotal() {
		return precoTotal;
	}
	public void setPrecoTotal(String precoTotal) {
		this.precoTotal = precoTotal;
	}
	
}
