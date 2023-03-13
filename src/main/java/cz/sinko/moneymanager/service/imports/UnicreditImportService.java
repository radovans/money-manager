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
public class UnicreditImportService implements ImportService {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public List<Transaction> parseTransactions(MultipartFile file) {
		List<String[]> rows = CsvUtil.getRowsFromCsv(file, ';', StandardCharsets.UTF_8);
		List<Transaction> transactions = new ArrayList<>();
		List<String[]> failedRows = new ArrayList<>();
		Account unicredit = accountRepository.findByName("Unicredit");
		rows.stream().skip(4).forEach(transaction -> {
			try {
				Transaction transactionEntity = new Transaction();
				transactionEntity.setDate(LocalDate.parse(transaction[3], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				transactionEntity.setRecipient(transaction[9].stripTrailing());
				transactionEntity.setNote(transaction[13].stripTrailing().stripLeading());
				transactionEntity.setAmount(CsvUtil.parseAmount(transaction[1]));
				transactionEntity.setAmountInCzk(null);
				transactionEntity.setCurrency("EUR");
				transactionEntity.setCategory(null);
				transactionEntity.setSubcategory(null);
				transactionEntity.setAccount(unicredit);
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