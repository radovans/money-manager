package cz.sinko.moneymanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.model.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public List<Account> findAccounts() {
		List<Account> accounts = accountRepository.findAll();
		return accounts;
	}

	public Account findAccount(Long accountId) throws ResourceNotFoundException {
		Optional<Account> account = accountRepository.findById(accountId);
		if (account.isEmpty()) {
			throw ResourceNotFoundException.createWith("Account", " with id '" + accountId + "' was not found");
		}
		return account.get();
	}
}
