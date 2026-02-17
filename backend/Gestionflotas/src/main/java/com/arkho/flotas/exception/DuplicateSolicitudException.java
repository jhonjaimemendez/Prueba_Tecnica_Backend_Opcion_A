package com.arkho.flotas.exception;

public class DuplicateSolicitudException extends RuntimeException {
  
	private static final long serialVersionUID = 1L;

	public DuplicateSolicitudException(String message) {
        super(message);
    }
}