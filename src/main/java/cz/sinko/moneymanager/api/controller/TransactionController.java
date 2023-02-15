package cz.sinko.moneymanager.api.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.RequestValidationException;
import cz.sinko.moneymanager.api.mapper.TransactionMapper;
import cz.sinko.moneymanager.api.response.AccountTransactionDto;
import cz.sinko.moneymanager.api.response.AccountTransactionsDto;
import cz.sinko.moneymanager.api.response.SortTransactionFields;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
@Validated
@Slf4j
public class TransactionController {

	private final TransactionService transactionService;

	@GetMapping
	public ResponseEntity<?> getTransactions(
			@RequestParam(required = false) String sort,
			@RequestParam(defaultValue = "0") String page,
			@RequestParam(defaultValue = Integer.MAX_VALUE + "") String size,
			@RequestParam(required = false) String search,
			@RequestParam(defaultValue = "2020-01-01T00:00:00.000Z") String from,
			@RequestParam(defaultValue = "2023-12-31T23:59:59.999Z") String to,
			@RequestParam(required = false) String category) throws RequestValidationException {
		String parsedSort = parseSort(sort);
		String parsedDirection = parseDirection(sort);
		validateRequest(parsedSort, parsedDirection, page, size, from, to);
		log.info("Finding all transactions with sort: '{}', direction: '{}', page: '{}'. size: '{}', search: '{}', from: '{}', to: '{}'.", parsedSort, parsedDirection, page, size, search, from, to);
		Page<Transaction> transactions = transactionService.findTransactions(parsedSort, Sort.Direction.fromString(parsedDirection), Integer.parseInt(page), Integer.parseInt(size), search, LocalDate.from(OffsetDateTime.parse(from)), LocalDate.from(OffsetDateTime.parse(to)), category);
		List<AccountTransactionDto> accountTransactionDtos = TransactionMapper.t().map(transactions);
		AccountTransactionsDto response = new AccountTransactionsDto();
		response.setTransactions(accountTransactionDtos);
		response.setTotal(transactions.getTotalElements());
		response.setTotalAmount(transactions.getTotalElements() > 0 ? transactions.getContent().stream().map(Transaction::getAmountInCzk).reduce(BigDecimal::add).get() : BigDecimal.ZERO);
		return ResponseEntity.ok().headers(createHeaders(transactions)).body(response);
	}

	private String parseDirection(String sort) {
		if (sort != null) {
			return sort.split(",")[1].split(":")[1].replace("\"", "").replace("}", "");
		} else {
			return Sort.Direction.ASC.name();
		}
	}

	private String parseSort(String sort) {
		if (sort != null) {
			String parsedSort = sort.split(",")[0].split(":")[1].replace("\"", "");
			if (parsedSort.equals(SortTransactionFields.formattedAmountInCzk.name())) {
				return SortTransactionFields.amount.name();
			}
			return parsedSort;
		} else {
			return SortTransactionFields.id.name();
		}
	}

	private void validateRequest(String sort, String direction, String page, String size, String from, String to) throws RequestValidationException {
		List<ObjectError> errors = new ArrayList<>();
		ValidationUtils.validateEnumValue(sort, errors, SortTransactionFields.class);
		ValidationUtils.validateEnumValue(direction, errors, Sort.Direction.class);
		ValidationUtils.validateIntegerGreaterThan(page, 0, errors, "page");
		ValidationUtils.validateIntegerGreaterThan(size, 0, errors, "size");
		ValidationUtils.validateOffsetDateTimeFormat(from, errors, "from");
		ValidationUtils.validateOffsetDateTimeFormat(to, errors, "to");
		ValidationUtils.checkErrors(errors);
	}

	private static HttpHeaders createHeaders(Page<Transaction> transactions) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_RANGE,
				"0-" + transactions.getNumberOfElements() + "/" + transactions.getTotalElements());
		return headers;
	}

}
