package br.com.rar.soldi.shopline.controller.model;

import java.io.Serializable;
import java.util.List;

public class StatusPagamentoEventoRequest implements Serializable {

	private static final long serialVersionUID = 5381839097628262554L;
	
	private List<DadosStatusPagamentoEvento> dados;

	public List<DadosStatusPagamentoEvento> getDados() {
		return dados;
	}

	public void setDados(List<DadosStatusPagamentoEvento> dados) {
		this.dados = dados;
	}
	
}
