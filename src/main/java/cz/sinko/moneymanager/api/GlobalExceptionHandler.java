package cz.sinko.moneymanager.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}

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
		} else if (ex instanceof ConstraintViolationException) {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			ConstraintViolationException exception = (ConstraintViolationException) ex;

			return handleConstraintViolation(exception, headers, status, request);
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
		String stackTrace = getStackTrace(ex);
		return handleExceptionInternal(ex, new ApiError(errors, stackTrace), headers, status, request);
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

	protected ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
			HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> errorMessages = new ArrayList<>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errorMessages.add(violation.getPropertyPath() + ": " + violation.getMessage());
		}

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