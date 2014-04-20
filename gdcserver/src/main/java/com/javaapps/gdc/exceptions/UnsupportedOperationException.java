package com.javaapps.gdc.exceptions;

public class UnsupportedOperationException extends RuntimeException {

	public UnsupportedOperationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		}

	public UnsupportedOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedOperationException(String message) {
		super(message);
	}

	public UnsupportedOperationException(Throwable cause) {
		super(cause);
	}

}
