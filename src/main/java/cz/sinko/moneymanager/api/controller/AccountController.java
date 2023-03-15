package cz.sinko.moneymanager.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.facade.AccountFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {

	private final AccountFacade accountFacade;

	@GetMapping
	public List<AccountDto> getAccounts() {
		log.info("Finding all accounts.");
		return accountFacade.getAccounts();
	}

	@PostMapping
	public AccountDto createAccount(@RequestBody AccountDto accountDto) {
		log.info("Creating new account: '{}'.", accountDto);
		return accountFacade.createAccount(accountDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
		log.info("Deleting account with id: '{}'.", id);
		accountFacade.deleteAccount(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public AccountDto updateAccount(@PathVariable Long id, @RequestBody AccountDto accountDto)
			throws ResourceNotFoundException {
		log.info("Updating rule with id: '{}'.", id);
		return accountFacade.updateAccount(id, accountDto);
	}

}
