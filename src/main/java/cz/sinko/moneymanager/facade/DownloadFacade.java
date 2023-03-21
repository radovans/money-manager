package cz.sinko.moneymanager.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CsvUtil;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class DownloadFacade {

	public static final String EXPORT_CSV_FILE = "C:\\Users\\radovan.sinko\\Downloads\\transactions-export.csv";

	private final TransactionService transactionService;

	public void downloadTransactions() {
		List<Transaction> transactions = transactionService.find();
		CsvUtil.createCsvFile(transactions, EXPORT_CSV_FILE);
	}
}
