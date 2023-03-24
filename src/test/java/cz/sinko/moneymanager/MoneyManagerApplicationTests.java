package cz.sinko.moneymanager;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Testcontainers;

import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.facade.AccountFacade;
import cz.sinko.moneymanager.repository.AccountRepository;
import io.restassured.filter.log.LogDetail;

@Testcontainers
class MoneyManagerApplicationTests extends AbstractIntegrationTest {

	@Autowired
	public AccountRepository accountRepository;

	@Autowired
	public AccountFacade accountFacade;

	@Test
	void contextLoads() {
		AccountDto account = accountFacade.createAccount(AccountDto.builder().id(null).name("test").build());
		Assertions.assertTrue(accountRepository.findByName("test").isPresent(), "there should be 'test' account");
	}

	@Test
	public void testAppHealth() {
		given(requestSpecification)
				.when()
				.get("/actuator/health")
				.then()
				.statusCode(200)
				.body("status", equalTo("UP"))
				.log().ifValidationFails(LogDetail.ALL);
	}

	@Test
	public void testRepository() {
		given(requestSpecification)
				.when()
				.get("/accounts")
				.then()
				.statusCode(200)
				.log().ifValidationFails(LogDetail.ALL);
	}

}
