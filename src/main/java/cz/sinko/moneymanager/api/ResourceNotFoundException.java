package cz.sinko.moneymanager.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

	private final String resourceName;
	private final String message;

	private ResourceNotFoundException(String resourceName, String message) {
		this.resourceName = resourceName;
		this.message = message;
	}

	public static ResourceNotFoundException createWith(String resourceName, String message) {
		return new ResourceNotFoundException(resourceName, message);
	}

	@Override
	public String getMessage() {
		return resourceName + " " + message;
	}
}
