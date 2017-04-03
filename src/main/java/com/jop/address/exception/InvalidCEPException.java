package com.jop.address.exception;

import org.springframework.http.HttpStatus;

/**
 * Invalid CEP exception.
 * 
 * @author julianopontes
 *
 */
public class InvalidCEPException extends WebException {

	private static final long serialVersionUID = -6399149462145486042L;

	public InvalidCEPException(String cep) {
		super(HttpStatus.PRECONDITION_FAILED, String.format("Invalid CEP: %s", cep));
	}
}