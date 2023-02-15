package cz.sinko.moneymanager.api;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({
			ResourceNotFoundException.class,
			RequestValidationException.class,
			ConstraintViolationException.class,
	})
	@Nullable
	public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
		HttpHeaders headers = new HttpHeaders();

		log.error("Handling " + ex.getClass().getSimpleName() + " due to " + ex.getMessage());

		if (ex instanceof ResourceNotFoundException) {
			HttpStatus status = HttpStatus.NOT_FOUND;
			ResourceNotFoundException exception = (ResourceNotFoundException) ex;

			return handleResourceNotFoundException(exception, headers, status, request);
		} else if (ex instanceof RequestValidationException) {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			RequestValidationException exception = (RequestValidationException) ex;

			return handleRequestValidationException(exception, headers, status, request);
		} else {
			if (log.isWarnEnabled()) {
				log.warn("Unknown exception type: " + ex.getClass().getName());
			}

			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			return handleExceptionInternal(ex, null, headers, status, request);
		}
	}

	protected ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex,
			HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> errors = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
	}

	protected ResponseEntity<ApiError> handleRequestValidationException(RequestValidationException ex,
			HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> errorMessages = ex.getErrors()
				.stream()
				.map(contentError ->
						contentError.getObjectName() + " " + contentError.getDefaultMessage())
				.collect(Collectors.toList());

		return handleExceptionInternal(ex, new ApiError(errorMessages), headers, status, request);
	}

	protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, @Nullable ApiError body,
			HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
			request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
		}

		return new ResponseEntity<>(body, headers, status);
	}
}