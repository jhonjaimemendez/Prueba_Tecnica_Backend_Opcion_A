package com.arkho.flotas.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateSolicitudException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public Map<String, String> handleDuplicate(DuplicateSolicitudException duplicateSolicitudException) {
		return Map.of("Error", duplicateSolicitudException.getMessage());
	}

	@ExceptionHandler(InvalidSolicitudException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleInvalid(InvalidSolicitudException invalidSolicitudException) {
		return Map.of("Error", invalidSolicitudException.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> handleValidation() {
	    return Map.of("Error", "Datos invalidos");
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleGeneric(Exception exception) {
		return Map.of("Error", "Error interno del servidor");
	}

	
}
