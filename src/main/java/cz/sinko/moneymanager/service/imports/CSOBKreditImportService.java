package cz.sinko.moneymanager.service.imports;

import java.nio.charset.StandardCharsets;
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
public class CSOBKreditImportService implements ImportService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public List<Transaction> parseTransactions(MultipartFile file) {
		List<String[]> rows = CsvUtil.getRowsFromCsv(file, ';', StandardCharsets.UTF_8);
		List<Transaction> transactions = new ArrayList<>();
		List<String[]> failedRows = new ArrayList<>();
		Account csobKredit = accountRepository.findByName("ČSOB Kredit");
		rows.stream().skip(3).forEach(transaction -> {
			try {
				Transaction transactionEntity = new Transaction();
				transactionEntity.setDate(LocalDate.parse(transaction[1], DateTimeFormatter.ofPattern("dd.MM.yyyy")));
				transactionEntity.setRecipient(transaction[7]);
				transactionEntity.setNote(transaction[14] + " - " + transaction[11]);
				transactionEntity.setAmount(CsvUtil.parseAmount(transaction[2]));
				transactionEntity.setAmountInCzk(null);
				transactionEntity.setCurrency(null);
				transactionEntity.setMainCategory(null);
				transactionEntity.setCategory(null);
				transactionEntity.setAccount(csobKredit);
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