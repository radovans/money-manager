package cz.sinko.moneymanager.api.controller;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.facade.AccountFacade;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cz.sinko.moneymanager.api.ApiUris.ROOT_URI_ACCOUNTS;

@RestController
@RequestMapping(ROOT_URI_ACCOUNTS)
@AllArgsConstructor
@Validated
@Slf4j
public class AccountController {

	private final AccountFacade accountFacade;

	@GetMapping
	public ResponseEntity<List<AccountDto>> getAccounts() {
		log.info("Finding all accounts.");
		return ResponseEntity.ok().body(accountFacade.getAccounts());
	}

	@PostMapping
	public ResponseEntity<AccountDto> createAccount(@RequestBody @Valid AccountDto accountDto) {
		log.info("Creating new account: '{}'.", accountDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(accountFacade.createAccount(accountDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id) throws ResourceNotFoundException {
		log.info("Deleting account with id: '{}'.", id);
		accountFacade.deleteAccount(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody @Valid AccountDto accountDto)
			throws ResourceNotFoundException {
		log.info("Updating rule with id: '{}'.", id);
		return ResponseEntity.ok().body(accountFacade.updateAccount(id, accountDto));
	}

}
