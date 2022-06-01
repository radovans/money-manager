package cz.sinko.moneymanager;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.model.Transaction;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.TransactionRepository;

@SpringBootApplication
public class MoneyManagerApplication {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	public static void main(String[] args) {
		SpringApplication.run(MoneyManagerApplication.class, args);
	}

	@PostConstruct
	private void postConstruct() {
		var czAccount = accountRepository.save(new Account(null, "CZ", null));
		var skAccount = accountRepository.save(new Account(null, "SK", null));
		transactionRepository.saveAll(List.of(
				new Transaction(null, "transaction-1", czAccount),
				new Transaction(null, "transaction-2", czAccount),
				new Transaction(null, "transaction-3", czAccount),
				new Transaction(null, "transaction-4", skAccount),
				new Transaction(null, "transaction-5", skAccount)
		));
	}

}

