package br.com.rar.soldi.shopline.integration;

import br.com.rar.soldi.shopline.integration.model.Inscricao;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface InscricaoService {

	@GET("inscricao/{ref}")
	Call<Inscricao> buscarInscricao(@Path("ref") String ref);
	
}