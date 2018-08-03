package br.com.rar.soldi.shopline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.rar.soldi.shopline.service.PagamentoService;
import br.com.rar.soldi.shopline.service.exception.DadosObrigatoriosPagamentoException;
import br.com.rar.soldi.shopline.service.exception.InscricaoNaoEncontradaException;
import br.com.rar.soldi.shopline.service.exception.ServiceException;

@Controller
public class PagamentoController {

	@Value("${shoplineUrl}")
	private String shoplineUrl;
	
	@Autowired
	private PagamentoService pagamentoService;
		
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
	
	@GetMapping("/sucesso")
	public String sucesso() {
		return "sucesso-pagamento";
	}
	
	@PostMapping("/erro-popup")
	public ModelAndView erroPopup(String dadosPagamento) {
		ModelAndView mav = new ModelAndView("erro-popup");
		mav.addObject("shoplineUrl", this.shoplineUrl);
		mav.addObject("dadosPagamento", dadosPagamento);
		return mav;
	}	
	
}
