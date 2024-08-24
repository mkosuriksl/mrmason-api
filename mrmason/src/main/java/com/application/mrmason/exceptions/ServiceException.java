package com.application.mrmason.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ServiceException extends RuntimeException {

	private String message;

	public ServiceException(String message) {
		super(String.format(message));
		this.message = message;

	}

	public String getMessage() {
		return message;
	}

}
