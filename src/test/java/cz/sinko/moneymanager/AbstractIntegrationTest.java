package cz.sinko.moneymanager;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AbstractIntegrationTest {

	@Container
	public static PostgreSQLContainer<?> pgsql = new PostgreSQLContainer<>("postgres:latest");
	protected RequestSpecification requestSpecification;
	@LocalServerPort
	protected int localServerPort;

	@DynamicPropertySource
	static void configureTestContainersProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", pgsql::getJdbcUrl);
		registry.add("spring.datasource.username", pgsql::getUsername);
		registry.add("spring.datasource.password", pgsql::getPassword);
	}

	@BeforeEach
	public void setUpAbstractIntegrationTest() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		requestSpecification = new RequestSpecBuilder()
				.setPort(localServerPort)
				.addHeader(
						HttpHeaders.CONTENT_TYPE,
						MediaType.APPLICATION_JSON_VALUE
				)
				.build();
	}

}
