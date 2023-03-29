package cz.sinko.moneymanager.config;

import static java.util.Collections.singleton;
import static org.zalando.logbook.BodyFilter.merge;
import static org.zalando.logbook.BodyFilters.defaultValue;
import static org.zalando.logbook.Conditions.exclude;
import static org.zalando.logbook.Conditions.requestTo;
import static org.zalando.logbook.json.JsonBodyFilters.replaceJsonStringProperty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

@Configuration
public class LogbookConfig {

	@Bean
	public Logbook logbook() {
		HttpLogFormatter formatter = new JsonHttpLogFormatter();
		LogstashLogbackSink sink = new LogstashLogbackSink(formatter);

		return Logbook.builder()
				.sink(sink)
				.condition(exclude(
						requestTo("/actuator"),
						requestTo("/actuator/*"),
						requestTo("/v3/api-docs"),
						requestTo("/swagger-ui.html"),
						requestTo("/swagger-resources"),
						requestTo("/csrf"),
						requestTo("/webjars/springfox-swagger-ui")))
				.bodyFilter(merge(defaultValue(),
						replaceJsonStringProperty(singleton("password"), "hidden-value")))
				.build();
	}

}
