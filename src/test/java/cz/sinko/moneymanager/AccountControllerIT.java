package cz.sinko.moneymanager;

import static cz.sinko.moneymanager.api.ApiUris.ROOT_URI_ACCOUNTS;
import static cz.sinko.moneymanager.api.ApiUris.SLASH;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Testcontainers;

import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.facade.AccountFacade;
import cz.sinko.moneymanager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Testcontainers
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AccountControllerIT extends AbstractIntegrationTest {

	public static final String CASH = "Cash";
	public static final String BANK_ACCOUNT = "Bank account";

	private final AccountFacade accountFacade;
	private final AccountRepository accountRepository;

	@BeforeEach
	public void deleteAccounts() {
		accountRepository.deleteAll();
	}

	//	HAPPY SCENARIOS
	@Test
	void createAccount() {
		AccountDto cashAccount = AccountDto.builder().name(CASH).build();

		AccountDto createdAccount = given(requestSpecification)
				.body(cashAccount)
				.when()
				.post(ROOT_URI_ACCOUNTS)
				.then()
				.statusCode(201)
				.extract().body().as(AccountDto.class);

		assertThat(createdAccount.getName()).isEqualTo(cashAccount.getName());
		assertThat(accountFacade.getAccounts()).contains(createdAccount);
	}

	@Test
	void updateAccount() {
		AccountDto cashAccount = AccountDto.builder().name(CASH).build();
		AccountDto createdCashAccount = accountFacade.createAccount(cashAccount);
		Long id = createdCashAccount.getId();

		AccountDto updatedAccount = given(requestSpecification)
				.body(cashAccount.toBuilder().name(BANK_ACCOUNT).build())
				.when()
				.put(ROOT_URI_ACCOUNTS + SLASH + id)
				.then()
				.statusCode(200)
				.extract().body().as(AccountDto.class);

		assertThat(updatedAccount.getName()).isEqualTo(BANK_ACCOUNT);
		assertThat(updatedAccount.getId()).isEqualTo(id);
		assertThat(accountFacade.getAccounts())
				.contains(updatedAccount);
	}

	@Test
	void deleteAccount() {
		AccountDto cashAccount = AccountDto.builder().name(CASH).build();
		AccountDto createdCashAccount = accountFacade.createAccount(cashAccount);
		Long id = createdCashAccount.getId();

		given(requestSpecification)
				.when()
				.delete(ROOT_URI_ACCOUNTS + SLASH + id)
				.then()
				.statusCode(200);

		assertThat(accountFacade.getAccounts())
				.doesNotContain(createdCashAccount);
	}

	@Test
	void getAccounts() {
		AccountDto cashAccount = AccountDto.builder().name(CASH).build();
		AccountDto bankAccount = AccountDto.builder().name(BANK_ACCOUNT).build();
		AccountDto createdCashAccount = accountFacade.createAccount(cashAccount);
		AccountDto createdBankAccount = accountFacade.createAccount(bankAccount);

		List<AccountDto> accounts = given(requestSpecification)
				.when()
				.get(ROOT_URI_ACCOUNTS)
				.then()
				.statusCode(200)
				.extract().body()
				.jsonPath().getList(".", AccountDto.class);

		assertThat(accounts).contains(createdCashAccount);
		assertThat(accounts).contains(createdBankAccount);
	}

	//	NEGATIVE SCENARIOS
	@Test
	void createAccountWithEmptyName() {
		AccountDto accountDto = AccountDto.builder().name("").build();

		given(requestSpecification)
				.body(accountDto)
				.when()
				.post(ROOT_URI_ACCOUNTS)
				.then()
				.statusCode(400);

		assertThat(accountFacade.getAccounts()).doesNotContain(accountDto);
	}

	@Test
	void createAccountWithNonUniqueName() {
		AccountDto cashAccount = AccountDto.builder().name(CASH).build();
		accountFacade.createAccount(cashAccount);

		given(requestSpecification)
				.body(cashAccount)
				.when()
				.post(ROOT_URI_ACCOUNTS)
				.then()
				.statusCode(400);
	}

	@Test
	void updateAccountWithEmptyName() {
		AccountDto cashAccount = AccountDto.builder().name(CASH).build();
		AccountDto createdCashAccount = accountFacade.createAccount(cashAccount);
		Long id = createdCashAccount.getId();

		given(requestSpecification)
				.body(cashAccount.toBuilder().name("").build())
				.when()
				.put(ROOT_URI_ACCOUNTS + SLASH + id)
				.then()
				.statusCode(400);

		assertThat(accountFacade.getAccounts())
				.contains(createdCashAccount);
	}

	@Test
	void deleteAccountWhichDoesNotExist() {
		long id = -1L;

		given(requestSpecification)
				.when()
				.delete(ROOT_URI_ACCOUNTS + SLASH + id)
				.then()
				.statusCode(404);
	}

	@Test
	void updateAccountWhichDoesNotExist() {
		long id = -1L;
		AccountDto cashAccount = AccountDto.builder().name(CASH).build();

		given(requestSpecification)
				.body(cashAccount)
				.when()
				.put(ROOT_URI_ACCOUNTS + SLASH + id)
				.then()
				.statusCode(404);
	}
}
