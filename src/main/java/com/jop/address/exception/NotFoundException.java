package com.jop.address.exception;

import org.springframework.http.HttpStatus;

/**
 * Not found exception.
 * 
 * @author julianopontes
 *
 */
public class NotFoundException extends WebException {

	private static final long serialVersionUID = 5860003868436053729L;

	public NotFoundException(String domain) {
		super(HttpStatus.NOT_FOUND, String.format("%s not found.", domain));
	}
}