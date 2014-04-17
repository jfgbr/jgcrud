package com.jgalante.jgcrud.exception;

import java.util.Set;

import javax.validation.ConstraintViolation;

public class ConstraintViolationException extends javax.validation.ConstraintViolationException{
	
	private static final long serialVersionUID = 1L;

	public ConstraintViolationException(String messages,
			Set<ConstraintViolation<?>> constraintViolations) {
		super(messages, constraintViolations);
	}
	
	public ConstraintViolationException(
			Set<ConstraintViolation<?>> constraintViolations) {
		super(constraintViolations);
	}

}
