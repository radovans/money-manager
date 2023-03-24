package cz.sinko.moneymanager.api.filters;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class RequestIdFilter extends OncePerRequestFilter {

	private static final String REQUEST_HEADER_NAME = "X-Request-Id";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			final FilterChain filterChain)
			throws ServletException, IOException {
		if (!SkipLoggingFilter.skip(request)) {
			String requestId = request.getHeader(REQUEST_HEADER_NAME);

			if (requestId == null || requestId.isEmpty() || requestId.isBlank()) {
				requestId = UUID.randomUUID().toString();
			}
			try {
				MDC.put("requestId", requestId);
				response.addHeader(REQUEST_HEADER_NAME, requestId);
				filterChain.doFilter(request, response);
			} finally {
				MDC.clear();
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

}