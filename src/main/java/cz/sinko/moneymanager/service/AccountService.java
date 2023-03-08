package cz.sinko.moneymanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.AccountMapperImpl;
import cz.sinko.moneymanager.api.response.AccountDto;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.model.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public List<Account> getAccounts(Sort sort) {
		return accountRepository.findAll(sort);
	}

	public Account getAccount(Long accountId) throws ResourceNotFoundException {
		Optional<Account> account = accountRepository.findById(accountId);
		if (account.isEmpty()) {
			throw ResourceNotFoundException.createWith("Account", " with id '" + accountId + "' was not found");
		}
		return account.get();
	}

	public Account createAccount(AccountDto accountDto) throws ResourceNotFoundException {
		Account account = new AccountMapperImpl().map(accountDto);
		return accountRepository.save(account);
	}

	public void deleteAccount(Long id) {
		accountRepository.deleteById(id);
	}

	public Account updateAccount(Long id, AccountDto accountDto) throws ResourceNotFoundException {
		Account account = accountRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.createWith("Account",
				" with id '" + accountDto.getId() + "' was not found"));
		Account newAccount = new AccountMapperImpl().map(accountDto);
		account.setName(newAccount.getName());
		return accountRepository.save(account);
	}

}
