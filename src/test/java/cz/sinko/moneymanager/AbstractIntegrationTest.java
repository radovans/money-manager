package cz.sinko.moneymanager;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.SubcategoryRepository;
import cz.sinko.moneymanager.repository.TransactionRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AbstractIntegrationTest {

	@Container
	public static PostgreSQLContainer<?> pgsql = new PostgreSQLContainer<>("postgres:latest");
	protected RequestSpecification requestSpecification;
	@LocalServerPort
	protected int localServerPort;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SubcategoryRepository subcategoryRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@DynamicPropertySource
	static void configureTestContainersProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", pgsql::getJdbcUrl);
		registry.add("spring.datasource.username", pgsql::getUsername);
		registry.add("spring.datasource.password", pgsql::getPassword);
	}

	@PostConstruct
	public void clearDatabase() {
		transactionRepository.deleteAll();
		subcategoryRepository.deleteAll();
		categoryRepository.deleteAll();
		accountRepository.deleteAll();
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
