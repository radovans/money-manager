package cz.sinko.moneymanager.api.controller;

import static cz.sinko.moneymanager.api.ApiUris.ROOT_URI_ACCOUNTS;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.facade.AccountFacade;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(ROOT_URI_ACCOUNTS)
@AllArgsConstructor
@Validated
@Slf4j
public class AccountController {

	private final AccountFacade accountFacade;

	@GetMapping
	public List<AccountDto> getAccounts() {
		log.info("Finding all accounts.");
		return accountFacade.getAccounts();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public AccountDto createAccount(@RequestBody @Valid AccountDto accountDto) {
		log.info("Creating new account: '{}'.", accountDto);
		return accountFacade.createAccount(accountDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAccount(@PathVariable Long id) throws ResourceNotFoundException {
		log.info("Deleting account with id: '{}'.", id);
		accountFacade.deleteAccount(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public AccountDto updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDto accountDto)
			throws ResourceNotFoundException {
		log.info("Updating rule with id: '{}'.", id);
		return accountFacade.updateAccount(id, accountDto);
	}

}
