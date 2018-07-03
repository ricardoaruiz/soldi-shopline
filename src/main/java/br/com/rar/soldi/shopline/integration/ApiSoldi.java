package br.com.rar.soldi.shopline.integration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Service
public class ApiSoldi {

	@Value("${soldiApiBaseUrl}")
	private String soldiApiBaseUrl;
	
	private Retrofit retrofit;
		
	@PostConstruct
	public void teste() {		
		this.retrofit = new Retrofit.Builder()
				.baseUrl(soldiApiBaseUrl)
				.addConverterFactory(JacksonConverterFactory.create())
				.build();
		
	}
	
	public InscricaoService getInscricaoService() {
		return this.retrofit.create(InscricaoService.class);
	}
	
}
