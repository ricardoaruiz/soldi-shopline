package br.com.rar.soldi.shopline.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.rar.soldi.shopline.controller.model.DadosStatusPagamentoEventoRetorno;
import br.com.rar.soldi.shopline.controller.model.StatusPagamentoEventoRequest;
import br.com.rar.soldi.shopline.controller.model.StatusPagamentoEventoResponse;
import br.com.rar.soldi.shopline.service.PagamentoService;
import br.com.rar.soldi.shopline.service.exception.DadosObrigatoriosPagamentoException;
import br.com.rar.soldi.shopline.service.exception.InscricaoNaoEncontradaException;
import br.com.rar.soldi.shopline.service.exception.ServiceException;

@Controller
public class PagamentoController {

	/**
	 * Url do shopline para realização da integração
	 */
	@Value("${shoplineUrl}")
	private String shoplineUrl;
	
	/**
	 * Serviço de pagamento
	 */
	@Autowired
	private PagamentoService pagamentoService;
		
	/**
	 * Faz a emissão do boleto para a inscrição informada
	 * @param reference
	 * @return
	 */
	@GetMapping("/pagamento")
	public ModelAndView pagamento(@RequestParam("reference") String reference) {
		
		ModelAndView mav = new ModelAndView("inicio-pagamento");

		try {
			String dadosPagamento = this.pagamentoService.obtemDadosPagamento(reference);
			mav.addObject("shoplineUrl", this.shoplineUrl);
			mav.addObject("dadosPagamento", dadosPagamento);
		} catch (ServiceException | InscricaoNaoEncontradaException | DadosObrigatoriosPagamentoException e) {
			mav.setViewName("erro-pagamento");
			mav.addObject("dadosErro", e.getMessage());
		}
						
		return mav;
	}
	
	/**
	 * Consulta o status do pagamento das inscrições do evento informado.
	 * @param request
	 * @return
	 */
	@PostMapping("/status-pagamento")
	public ResponseEntity<StatusPagamentoEventoResponse> obtemStatusPagamentoEvento(
			@RequestBody StatusPagamentoEventoRequest request) {		
		try {		
			StatusPagamentoEventoResponse statusPagamentoEventoResponse = new StatusPagamentoEventoResponse();
			List<DadosStatusPagamentoEventoRetorno> dadosStatusPagamentoRetorno = this.pagamentoService.obtemDadosConsultaStatus(request);
			statusPagamentoEventoResponse.getDados().addAll(dadosStatusPagamentoRetorno);
			
			return ResponseEntity.ok(statusPagamentoEventoResponse);
		} catch (ServiceException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
		}		
	}
	
	/**
	 * Redireciona para a tela de sucesso
	 * @return
	 */
	@GetMapping("/sucesso")
	public String sucesso() {
		return "sucesso-pagamento";
	}
	
	/**
	 * Redireciona para a tela de erro de popup do browser
	 * @param dadosPagamento
	 * @return
	 */
	@PostMapping("/erro-popup")
	public ModelAndView erroPopup(String dadosPagamento) {
		ModelAndView mav = new ModelAndView("erro-popup");
		mav.addObject("shoplineUrl", this.shoplineUrl);
		mav.addObject("dadosPagamento", dadosPagamento);
		return mav;
	}	
	
}
