package br.com.rar.soldi.shopline.integration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Service
public class ApiSoldi {

	@Value("${soldiApiBaseUrl}")
	private String soldiApiBaseUrl;
	
	private Retrofit retrofit;
		
	@PostConstruct
	public void teste() {	
		
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor(); 
		logging.setLevel(Level.BODY);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder(); 
		httpClient.addInterceptor(logging);
		
		this.retrofit = new Retrofit.Builder()
				.baseUrl(soldiApiBaseUrl)
				.addConverterFactory(JacksonConverterFactory.create())
				.client(httpClient.build())
				.build();
		
	}
	
	public InscricaoService getInscricaoService() {
		return this.retrofit.create(InscricaoService.class);
	}
	
}
