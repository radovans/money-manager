package cz.sinko.moneymanager.api.filters;

import jakarta.servlet.http.HttpServletRequest;

public class SkipLoggingFilter {

	public static boolean skip(HttpServletRequest request) {
		String requestUrl = request.getRequestURI();
		return requestUrl != null && (requestUrl.contains("/actuator")
				|| requestUrl.contains("/v3/api-docs")
				|| requestUrl.contains("/swagger-ui.html")
				|| requestUrl.contains("/swagger-resources")
				|| requestUrl.contains("/csrf")
				|| requestUrl.contains("/webjars/springfox-swagger-ui")
				|| (requestUrl.endsWith("/") && requestUrl.length() == 1));
	}

}