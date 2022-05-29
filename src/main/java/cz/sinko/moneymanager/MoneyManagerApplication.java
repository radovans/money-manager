package cz.sinko.moneymanager;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.repository.AccountRepository;

@SpringBootApplication
public class MoneyManagerApplication {

	private final Map<String, String> properties = new HashMap<>();

	@Autowired
	private AccountRepository accountRepository;

	public static void main(String[] args) {
		SpringApplication.run(MoneyManagerApplication.class, args);
	}

	@PostConstruct
	private void postConstruct() {
		if (accountRepository.findAll().isEmpty()) {
			accountRepository.save(new Account(null, "CZ"));
			accountRepository.save(new Account(null, "SK"));
		}
	}
}

