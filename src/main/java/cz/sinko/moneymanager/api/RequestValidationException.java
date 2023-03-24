package cz.sinko.moneymanager.api;

import java.util.List;

import org.springframework.validation.ObjectError;

public class RequestValidationException extends Exception {

	final List<ObjectError> errors;

	private RequestValidationException(List<ObjectError> errors) {
		this.errors = errors;
	}

	public static RequestValidationException createWith(List<ObjectError> errors) {
		return new RequestValidationException(errors);
	}

	public List<ObjectError> getErrors() {
		return errors;
	}

}
