package com.jop.address.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception.
 * 
 * @author julianopontes
 *
 */
public abstract class WebException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3049747295998265311L;

	private final HttpStatus status;
	private final String message;

	public WebException(HttpStatus status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}