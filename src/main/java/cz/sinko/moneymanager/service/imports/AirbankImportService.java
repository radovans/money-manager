package cz.sinko.moneymanager.service.imports;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cz.sinko.moneymanager.service.CsvUtil;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AirbankImportService implements ImportService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public List<Transaction> parseTransactions(MultipartFile file) {
		List<String[]> rows = CsvUtil.getRowsFromCsv(file, ';', Charset.forName("CP1250"));
		List<Transaction> transactions = new ArrayList<>();
		List<String[]> failedRows = new ArrayList<>();
		Account airBank = accountRepository.findByName("AirBank");
		rows.stream().skip(1).forEach(transaction -> {
			try {
				Transaction transactionEntity = new Transaction();
				transactionEntity.setDate(LocalDate.parse(transaction[0], DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				transactionEntity.setRecipient(transaction[9]);
				transactionEntity.setNote(transaction[19]);
				transactionEntity.setAmount(CsvUtil.parseAmount(transaction[5]));
				transactionEntity.setAmountInCzk(null);
				transactionEntity.setCurrency(null);
				transactionEntity.setMainCategory(null);
				transactionEntity.setCategory(null);
				transactionEntity.setAccount(airBank);
				transactionEntity.setLabel(null);
				transactions.add(transactionEntity);
			} catch (Exception e) {
				failedRows.add(transaction);
			}
		});
		if (!failedRows.isEmpty()) {
			failedRows.forEach(row -> log.error("Failed to parse transactions '{}'", row));
		}
		return transactions;
	}

}