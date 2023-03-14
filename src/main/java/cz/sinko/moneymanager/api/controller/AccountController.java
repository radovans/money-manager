package cz.sinko.moneymanager.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.RequestValidationException;
import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.AccountMapperImpl;
import cz.sinko.moneymanager.api.mapper.TransactionMapper;
import cz.sinko.moneymanager.api.response.AccountDto;
import cz.sinko.moneymanager.api.response.AccountWithTransactionsDto;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.AccountService;
import cz.sinko.moneymanager.service.StatisticsService;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {

	private final AccountService accountService;
	private final TransactionService transactionService;
	private final StatisticsService statisticsService;

	@GetMapping
	public List<AccountDto> getAccounts() {
		log.info("Finding all accounts.");
		List<Account> accounts = accountService.find(Sort.by("id").ascending());
		statisticsService.calculateIncomeExpense(accounts);
		return new AccountMapperImpl().map(accounts);
	}

	@PostMapping
	public AccountDto createAccount(@RequestBody AccountDto accountDto) throws ResourceNotFoundException {
		log.info("Creating new account: '{}'.", accountDto);
		return new AccountMapperImpl().mapAccount(accountService.createAccount(accountDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
		log.info("Deleting account with id: '{}'.", id);
		accountService.deleteAccount(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public AccountDto updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto)
			throws ResourceNotFoundException {
		log.info("Updating rule with id: '{}'.", id);
		return new AccountMapperImpl().mapAccount(accountService.updateAccount(id, accountDto));
	}

	@GetMapping("/{accountId}")
	public AccountWithTransactionsDto getAccountWithTransactions(@PathVariable String accountId)
			throws ResourceNotFoundException, RequestValidationException {
		validateRequest(accountId);
		log.info("Finding account with id '{}'", accountId);
		Long id = Long.parseLong(accountId);
		Account account = accountService.find(id);
		statisticsService.calculateIncomeExpense(account);
		List<Transaction> transactions = transactionService.find(id);
		AccountWithTransactionsDto response = new AccountMapperImpl().mapAccountWithTransactions(account);
		response.setTransactions(TransactionMapper.t().mapTransactionWithoutAccount(transactions));
		return response;
	}

	private void validateRequest(String accountId) throws RequestValidationException {
		List<ObjectError> errors = new ArrayList<>();
		ValidationUtils.validateLongGreaterThan(accountId, 0L, errors, "accountId");
		ValidationUtils.checkErrors(errors);
	}

}
