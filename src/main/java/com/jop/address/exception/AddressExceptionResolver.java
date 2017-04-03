package com.jop.address.exception;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * Multiplat exception resolver.
 * 
 * @author julianopontes
 *
 */
public class AddressExceptionResolver extends DefaultHandlerExceptionResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(AddressExceptionResolver.class);

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
	    Exception ex) {
		ModelAndView view = new ModelAndView();

		try {
			if (WebException.class.isAssignableFrom(ex.getClass())) {
				WebException webex = (WebException) ex;
				response.sendError(webex.getStatus().value(), ex.getMessage());
			} else if (MethodArgumentNotValidException.class.isAssignableFrom(ex.getClass())) {
				MethodArgumentNotValidException methodex = (MethodArgumentNotValidException) ex;
				StringBuilder message = new StringBuilder().append(methodex.getBindingResult().getAllErrors().stream()
				    .map(ObjectError::getDefaultMessage).collect(Collectors.joining("\n")));
				response.sendError(HttpStatus.BAD_REQUEST.value(), message.toString());
			} else if (DuplicateKeyException.class.isAssignableFrom(ex.getClass())) {
				response.sendError(HttpStatus.CONFLICT.value(), ex.getMessage());
			} else {
				view = super.doResolveException(request, response, handler, ex);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}

		return view;
	}
}