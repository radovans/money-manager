package cz.sinko.moneymanager;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.repository.AccountRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

@Testcontainers
class MoneyManagerApplicationTests extends AbstractIntegrationTest {

	@Autowired
	public AccountRepository accountRepository;

	@Test
	void contextLoads() {
		Assertions.assertTrue(!accountRepository.findAll().iterator().hasNext(), () -> "there should be no data");
		Account account = accountRepository.save(Account.builder().id(null).name("test").build());
		Iterable<Account> all = accountRepository.findAll();
		Assertions.assertTrue(all.iterator().hasNext(), () -> "there should be some data");
	}

	@Test
	public void healthy() {
		given(requestSpecification)
				.when()
				.get("/actuator/health")
				.then()
				.statusCode(200)
				.log().ifValidationFails(LogDetail.ALL);
	}

}
