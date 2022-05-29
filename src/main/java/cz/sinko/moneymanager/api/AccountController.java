package cz.sinko.moneymanager.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {

	private final AccountRepository accountRepository;

	@GetMapping
	public Iterable<Account> getAccounts() {
		log.info("Finding all accounts.");
		return accountRepository.findAll();
	}
}
