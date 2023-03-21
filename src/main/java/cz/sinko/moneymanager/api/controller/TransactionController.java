package cz.sinko.moneymanager.api.controller;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountTransactionsDto;
import cz.sinko.moneymanager.api.dto.TransactionDto;
import cz.sinko.moneymanager.facade.TransactionFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
@Validated
@Slf4j
public class TransactionController {

	private final TransactionFacade transactionFacade;

	@GetMapping
	public AccountTransactionsDto getTransactions(
			@RequestParam(required = false) String sort,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "#{T(Integer).MIN_VALUE}") int size,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "2020-01-01T00:00:00.000Z") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
			@RequestParam(required = false) String category)
			throws ResourceNotFoundException {
		log.info("Finding all transactions with sort: '{}', page: '{}'. size: '{}', search: '{}', from: '{}', to: '{}'.", sort, page, size, search, from, to);
		return transactionFacade.getTransactions(sort, page, size, search, from, to, category);
	}

	@PostMapping
	public TransactionDto createTransaction(@RequestBody TransactionDto transactionDto)
			throws ResourceNotFoundException {
		log.info("Creating new Transaction: '{}'.", transactionDto);
		return transactionFacade.createTransaction(transactionDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
		log.info("Deleting Transaction with id: '{}'.", id);
		transactionFacade.deleteTransaction(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public TransactionDto updateTransaction(@PathVariable Long id, @RequestBody TransactionDto transactionDto)
			throws ResourceNotFoundException {
		log.info("Updating Transaction with id: '{}'.", id);
		return transactionFacade.updateTransaction(id, transactionDto);
	}

}
