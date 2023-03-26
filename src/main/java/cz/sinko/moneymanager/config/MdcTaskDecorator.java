package cz.sinko.moneymanager.config;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

class MdcTaskDecorator implements TaskDecorator {

	@Override
	public Runnable decorate(Runnable runnable) {
		Map<String, String> contextMap = MDC.getCopyOfContextMap();
		return () -> {
			try {
				MDC.setContextMap(contextMap);
				runnable.run();
			} finally {
				MDC.clear();
			}
		};
	}
}