package br.com.rar.soldi.shopline.integration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Service
public class ApiConsultaStatusShopline {

	@Value("${shoplineStatusUrl}")
	private String shoplineStatusUrl = "https://shopline.itau.com.br/shopline/consulta.aspx/";
	
	private Retrofit retrofit;
		
	@PostConstruct
	public void teste() {		
		
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor(); 
		logging.setLevel(Level.BODY);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder(); 
		httpClient.addInterceptor(logging);
		
		this.retrofit = new Retrofit.Builder()
				.baseUrl(shoplineStatusUrl)
				.addConverterFactory(ScalarsConverterFactory.create())
				.client(httpClient.build())
				.build();
		
	}
	
	public StatusPagamentoService getStatusPagamento() {
		return this.retrofit.create(StatusPagamentoService.class);
	}
	
}
