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

	/**
	 * Url para consulta de status dos pagamentos
	 */
	@Value("${shoplineStatusUrl}")
	private String shoplineStatusUrl;
	
	private Retrofit retrofit;
		
	@PostConstruct
	public void postConstruct() {		
		
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
