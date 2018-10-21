package br.com.rar.soldi.shopline.integration;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface StatusPagamentoService {

	@FormUrlEncoded
	@POST("{DC}")
	Call<String> consultaStatus(@Field("DC") String dc);
	
}