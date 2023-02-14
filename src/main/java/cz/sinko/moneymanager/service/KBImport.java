package cz.sinko.moneymanager.service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cz.sinko.moneymanager.CsvUtil;
import cz.sinko.moneymanager.model.Account;
import cz.sinko.moneymanager.model.Transaction;
import cz.sinko.moneymanager.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KBImport implements ImportService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public List<Transaction> parseTransactions(MultipartFile file) {
		List<String[]> rows = CsvUtil.getRowsFromCsv(file, ';', Charset.forName("CP1250"));
		List<Transaction> transactions = new ArrayList<>();
		List<String[]> failedRows = new ArrayList<>();
		Account komercniBanka = accountRepository.findByName("Komerční banka");
		rows.stream().skip(18).forEach(transaction -> {
			try {
				Transaction transactionEntity = new Transaction();
				transactionEntity.setDate(LocalDate.parse(transaction[0], DateTimeFormatter.ofPattern("dd.MM.yyyy")));
				transactionEntity.setRecipient(transaction[15].stripTrailing());
				transactionEntity.setNote(transaction[13].stripTrailing());
				transactionEntity.setAmount(CsvUtil.parseAmount(transaction[4]));
				transactionEntity.setAmountInCzk(null);
				transactionEntity.setCurrency(null);
				transactionEntity.setMainCategory(null);
				transactionEntity.setCategory(null);
				transactionEntity.setAccount(komercniBanka);
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