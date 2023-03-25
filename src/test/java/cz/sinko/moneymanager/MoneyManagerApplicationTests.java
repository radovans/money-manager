package cz.sinko.moneymanager;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.restassured.filter.log.LogDetail;

@Testcontainers
class MoneyManagerApplicationTests extends AbstractIntegrationTest {

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

}
