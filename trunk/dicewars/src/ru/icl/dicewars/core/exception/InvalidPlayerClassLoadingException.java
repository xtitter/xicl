package ru.icl.dicewars.core.exception;

public class InvalidPlayerClassLoadingException extends RuntimeException {
	private static final long serialVersionUID = -2141787408419066437L;

	public InvalidPlayerClassLoadingException() {
	}
	
	public InvalidPlayerClassLoadingException(Throwable e) {
		super(e);
	}
}
