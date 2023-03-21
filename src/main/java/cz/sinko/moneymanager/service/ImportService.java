package cz.sinko.moneymanager.service;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Transaction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ImportService {

	public List<Transaction> parseTransactions(MultipartFile file, char separator, Charset charset, Account account, int skip, String datePattern, Function<String[], String> dateFunction, Function<String[], String> recipientFunction, Function<String[], String> noteFunction, Function<String[], String> amountFunction, String currency) {
		List<String[]> rows = CsvUtil.getRowsFromCsv(file, separator, charset);
		List<Transaction> transactions = new ArrayList<>();
		List<String[]> failedRows = new ArrayList<>();
		rows.stream().skip(skip).forEach(transaction -> {
			try {
				Transaction transactionEntity = new Transaction();
				transactionEntity.setDate(LocalDate.parse(dateFunction.apply(transaction), DateTimeFormatter.ofPattern(datePattern)));
				transactionEntity.setRecipient(recipientFunction.apply(transaction));
				transactionEntity.setNote(noteFunction.apply(transaction));
				transactionEntity.setAmount(CsvUtil.parseAmount(amountFunction.apply(transaction)));
				transactionEntity.setAmountInCzk(null);
				transactionEntity.setCurrency(currency);
				transactionEntity.setCategory(null);
				transactionEntity.setSubcategory(null);
				transactionEntity.setAccount(account);
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