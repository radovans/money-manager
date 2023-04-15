package cz.sinko.moneymanager.api.controller;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountTransactionsDto;
import cz.sinko.moneymanager.api.dto.TransactionDto;
import cz.sinko.moneymanager.facade.TransactionFacade;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
@Validated
@Slf4j
public class TransactionController {

	private final TransactionFacade transactionFacade;

	@GetMapping
	public ResponseEntity<AccountTransactionsDto> getTransactions(
			@RequestParam(required = false) String sort,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "#{T(Integer).MIN_VALUE}") int size,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "2020-01-01T00:00:00.000Z") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
			@RequestParam(required = false) String category)
			throws ResourceNotFoundException {
		log.info("Finding all transactions with sort: '{}', page: '{}'. size: '{}', search: '{}', from: '{}', to: '{}'.", sort, page, size, search, from, to);
		return ResponseEntity.ok().body(transactionFacade.getTransactions(sort, page, size, search, from, to, category));
	}

	@PostMapping
	public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto)
			throws ResourceNotFoundException {
		log.info("Creating new Transaction: '{}'.", transactionDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(transactionFacade.createTransaction(transactionDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
		log.info("Deleting Transaction with id: '{}'.", id);
		transactionFacade.deleteTransaction(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDto)
			throws ResourceNotFoundException {
		log.info("Updating Transaction with id: '{}'.", id);
		return ResponseEntity.ok().body(transactionFacade.updateTransaction(id, transactionDto));
	}

}
