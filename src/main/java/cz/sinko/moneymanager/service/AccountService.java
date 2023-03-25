package cz.sinko.moneymanager.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.api.mapper.AccountMapper;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.model.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public List<Account> find(Sort sort) {
		return accountRepository.findAll(sort);
	}

	public Account find(Long accountId) throws ResourceNotFoundException {
		return accountRepository.findById(accountId).orElseThrow(() -> ResourceNotFoundException.createWith("Account",
				" with id '" + accountId + "' was not found"));
	}

	public Account find(String account) throws ResourceNotFoundException {
		return accountRepository.findByName(account).orElseThrow(() -> ResourceNotFoundException.createWith("Account",
				" with name '" + account + "' was not found"));
	}

	public Account createAccount(AccountDto accountDto) {
		Account account = AccountMapper.t().map(accountDto);
		return accountRepository.save(account);
	}

	public void deleteAccount(Long id) throws ResourceNotFoundException {
		if (!accountRepository.existsById(id))
			throw ResourceNotFoundException.createWith("Account", " with id '" + id + "' was not found");
		accountRepository.deleteById(id);
	}

	public Account updateAccount(Long id, AccountDto accountDto) throws ResourceNotFoundException {
		Account account = find(id);
		account.setName(accountDto.getName());
		return accountRepository.save(account);
	}

}
