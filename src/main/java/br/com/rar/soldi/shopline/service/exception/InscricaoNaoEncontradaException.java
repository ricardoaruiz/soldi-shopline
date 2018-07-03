package br.com.rar.soldi.shopline.service.exception;

public class InscricaoNaoEncontradaException extends Exception {

	private static final long serialVersionUID = -4985120626616747222L;

	public InscricaoNaoEncontradaException() {
	}

	public InscricaoNaoEncontradaException(String message) {
		super(message);
	}

	public InscricaoNaoEncontradaException(Throwable cause) {
		super(cause);
	}

	public InscricaoNaoEncontradaException(String message, Throwable cause) {
		super(message, cause);
	}

	public InscricaoNaoEncontradaException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
