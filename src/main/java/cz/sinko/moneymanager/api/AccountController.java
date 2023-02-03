package cz.sinko.moneymanager.api;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.mapper.AccountMapper;
import cz.sinko.moneymanager.api.mapper.TransactionMapper;
import cz.sinko.moneymanager.api.response.AccountDto;
import cz.sinko.moneymanager.api.response.AccountWithTransactionsDto;
import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.model.Transaction;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;

	@GetMapping
	public List<AccountDto> getAccounts() {
		log.info("Finding all accounts.");
		List<Account> accounts = accountRepository.findAll();
		return AccountMapper.t().map(accounts);
	}

	@GetMapping("/{accountId}")
	public AccountWithTransactionsDto getAccountWithTransactions(@PathVariable Long accountId) {
		log.info("Finding account with id '{}'", accountId);
		Optional<Account> account = accountRepository.findById(accountId);
		if (account.isEmpty()) {
			throw new ResourceNotFoundException("Account with id '" + accountId + "' not found.");
		}

		List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
		AccountWithTransactionsDto response = AccountMapper.t().mapAccountWithTransactions(account.get());
		response.setTransactions(TransactionMapper.t().mapTransactionWithoutAccount(transactions));
		return response;
	}
}
