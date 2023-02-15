package cz.sinko.moneymanager.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

	private final String resourceName;
	private final Long resourceId;

	public static ResourceNotFoundException createWith(String resourceName, Long resourceId) {
		return new ResourceNotFoundException(resourceName, resourceId);
	}

	private ResourceNotFoundException(String resourceName, Long resourceId) {
		this.resourceName = resourceName;
		this.resourceId = resourceId;
	}

	@Override
	public String getMessage() {
		return resourceName + " with id '" + resourceId + "' was not found";
	}
}
