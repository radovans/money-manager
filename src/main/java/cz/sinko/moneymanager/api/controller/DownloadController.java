package cz.sinko.moneymanager.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CsvUtil;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/download")
@AllArgsConstructor
@Slf4j
public class DownloadController {

	public static final String EXPORT_CSV_FILE = "C:\\Users\\radovan.sinko\\Downloads\\transactions-export.csv";

	@Autowired
	private TransactionService transactionService;

	@GetMapping(value = "/transactions/csv")
	public ResponseEntity<?> downloadTransactions() {
		log.info("Creating csv file with transactions.");
		List<Transaction> transactions = transactionService.findTransactions();
		CsvUtil.createCsvFile(transactions, EXPORT_CSV_FILE);
		return ResponseEntity.ok().build();
	}

}
