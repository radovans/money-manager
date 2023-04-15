package cz.sinko.moneymanager.api.controller.todo;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.dto.PlannedTransactionDto;
import cz.sinko.moneymanager.api.mapper.PlannedTransactionMapper;
import cz.sinko.moneymanager.repository.PlannedTransactionRepository;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CsvUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO clean up
@RestController
@RequestMapping("/planned-transactions")
@AllArgsConstructor
@Slf4j
public class PlannedTransactionController {

	private static final String PLANNED_TRANSACTIONS_CSV_FILENAME = "planned-transactions.csv";

	// TODO create and call facade
	private final PlannedTransactionRepository plannedTransactionRepository;

	@GetMapping
	public List<PlannedTransactionDto> getPlannedTransactions() {
		log.info("Finding all planned transactions.");
		return PlannedTransactionMapper.t().map(plannedTransactionRepository.findAll());
	}

	@GetMapping(value = "/csv", produces = "text/csv")
	public ResponseEntity<InputStreamResource> createPlannedTransactionsCsv() {
		return createCsvFile(PlannedTransactionMapper.t().mapToTransactionList(plannedTransactionRepository.findAll()), PLANNED_TRANSACTIONS_CSV_FILENAME);
	}

	private ResponseEntity<InputStreamResource> createCsvFile(List<Transaction> transactions, String csvFilename) {
		InputStreamResource output = CsvUtil.createCsvOutput(transactions);
		return ResponseEntity
				.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFilename)
				.header(HttpHeaders.CONTENT_TYPE, "text/csv")
				.body(output);
	}

}
