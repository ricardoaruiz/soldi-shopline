package br.com.rar.soldi.shopline.controller.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StatusPagamentoEventoResponse implements Serializable {

	private static final long serialVersionUID = -3053922246943814663L;
	
	private List<DadosStatusPagamentoEventoRetorno> dados = new ArrayList<DadosStatusPagamentoEventoRetorno>();

	public List<DadosStatusPagamentoEventoRetorno> getDados() {
		return dados;
	}

	public void setDados(List<DadosStatusPagamentoEventoRetorno> dados) {
		this.dados = dados;
	}
	
}
