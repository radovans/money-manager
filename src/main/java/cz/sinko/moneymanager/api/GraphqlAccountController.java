package cz.sinko.moneymanager.api;

import java.util.List;
import java.util.Optional;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.model.Transaction;
import cz.sinko.moneymanager.model.TransactionInput;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.TransactionRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
class GraphqlAccountController {

	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;

	@QueryMapping
	List<Account> accounts() {
		return accountRepository.findAll();
	}

	@QueryMapping
	Optional<Account> accountById(@Argument Long id) {
		return accountRepository.findById(id);
	}

	@MutationMapping
	Transaction addTransaction(@Argument TransactionInput transaction) {
		var account = accountRepository.findById(transaction.getAccountId()).orElseThrow(() -> new IllegalArgumentException("account not found"));
		var t = Transaction.builder().note(transaction.getNote()).account(account).build();
		return transactionRepository.save(t);
	}
}
