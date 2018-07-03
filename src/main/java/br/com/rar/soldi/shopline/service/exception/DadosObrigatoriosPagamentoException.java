package br.com.rar.soldi.shopline.service.exception;

public class DadosObrigatoriosPagamentoException extends Exception {

	private static final long serialVersionUID = -2442892671469892493L;

	public DadosObrigatoriosPagamentoException() {
		super();
	}

	public DadosObrigatoriosPagamentoException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DadosObrigatoriosPagamentoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DadosObrigatoriosPagamentoException(String message) {
		super(message);
	}

	public DadosObrigatoriosPagamentoException(Throwable cause) {
		super(cause);
	}
	
}
