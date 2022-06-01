package cz.sinko.moneymanager.api;

import java.util.List;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.repository.AccountRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
class GraphqlAccountController {

	private final AccountRepository accountRepository;

	@QueryMapping
	List<Account> accounts() {
		return accountRepository.findAll();
	}
}
