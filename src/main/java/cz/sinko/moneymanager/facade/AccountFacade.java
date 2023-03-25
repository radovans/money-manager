package cz.sinko.moneymanager.facade;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.api.mapper.AccountMapper;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class AccountFacade {

	private final AccountService accountService;

	public List<AccountDto> getAccounts() {
		List<Account> accounts = accountService.find(Sort.by("id").ascending());
		return AccountMapper.t().map(accounts);
	}

	public AccountDto createAccount(AccountDto accountDto) {
		return AccountMapper.t().mapAccount(accountService.createAccount(accountDto));
	}

	public void deleteAccount(Long id) throws ResourceNotFoundException {
		accountService.deleteAccount(id);
	}

	public AccountDto updateAccount(Long id, AccountDto accountDto)
			throws ResourceNotFoundException {
		return AccountMapper.t().mapAccount(accountService.updateAccount(id, accountDto));
	}

}
