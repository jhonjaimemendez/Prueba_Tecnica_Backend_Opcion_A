package com.arkho.flotas.exception;

public class InvalidSolicitudException extends RuntimeException  {
	
	private static final long serialVersionUID = 1L;

	public InvalidSolicitudException(String message) {
        super(message);
    }

}
