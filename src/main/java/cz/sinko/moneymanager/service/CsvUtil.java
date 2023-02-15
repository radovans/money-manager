package cz.sinko.moneymanager.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import cz.sinko.moneymanager.repository.model.Transaction;

public class CsvUtil {

	public static final String TYPE = "text/csv";
	public static final String DATE = "Dátum";
	public static final String RECIPIENT = "Od/Komu";
	public static final String NOTE = "Poznámka";
	public static final String AMOUNT = "Čiastka";
	public static final String AMOUNT_IN_CZK = "Čiastka v CZK";
	public static final String CURRENCY = "Mena";
	public static final String MAIN_CATEGORY = "Hlavná kategória";
	public static final String CATEGORY = "Kategória";
	public static final String ACCOUNT = "Účet";
	public static final String LABEL = "Label";

	public static List<String[]> getRowsFromCsv(String path, char separator, Charset charset) {
		File file = new File(path);
		try {
			CSVReader reader = new CSVReaderBuilder(new FileReader(file.getPath(), charset)).withCSVParser(new CSVParserBuilder().withSeparator(separator).build()).build();
			return reader.readAll();
		} catch (IOException e) {
			throw new RuntimeException("Cannot open csv file", e);
		} catch (CsvException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String[]> getRowsFromCsv(MultipartFile file, char separator, Charset charset) {
		try {
			CSVReader reader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(file.getInputStream(), charset))).withCSVParser(new CSVParserBuilder().withSeparator(separator).build()).build();
			return reader.readAll();
		} catch (IOException e) {
			throw new RuntimeException("Cannot open csv file", e);
		} catch (CsvException e) {
			throw new RuntimeException(e);
		}
	}

	public static void createCsvFile(List<Transaction> transactions, String path) {
		File file = new File(path);
		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
			createRows(transactions, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static InputStreamResource createCsvOutput(List<Transaction> transactions) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (CSVWriter writer = new CSVWriter(new PrintWriter(out))) {
			createRows(transactions, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
		return new InputStreamResource(byteArrayOutputStream);
	}

	private static void createRows(List<Transaction> transactions, CSVWriter writer) {
		String[] header = { DATE, RECIPIENT, NOTE, AMOUNT, AMOUNT_IN_CZK, CURRENCY, MAIN_CATEGORY,
				CATEGORY, ACCOUNT, LABEL };
		writer.writeNext(header);

		for (Transaction transaction : transactions) {
			String[] data = {
					transaction.getDate() == null ? "" : transaction.getDate().format(DateTimeFormatter.ofPattern("d.M.yyyy")),
					transaction.getRecipient() == null ? "" : transaction.getRecipient(),
					transaction.getNote() == null ? "" : transaction.getNote(),
					transaction.getAmount() == null ? "" : transaction.getAmount().toString().replace(".", ","),
					transaction.getAmountInCzk() == null ? "" : transaction.getAmountInCzk().toString().replace(".", ",") + " Kč",
					transaction.getCurrency() == null ? "" : transaction.getCurrency(),
					transaction.getMainCategory() == null ? "" : transaction.getMainCategory().getName(),
					transaction.getCategory() == null ? "" : transaction.getCategory().getName(),
					transaction.getAccount() == null ? "" : transaction.getAccount().getName(),
					transaction.getLabel() == null ? "" : transaction.getLabel()};
			writer.writeNext(data);
		}
	}

	public static BigDecimal parseAmount(String amount) {
		return new BigDecimal(amount.replace(",", ".").replace(" ", ""));
	}

	public static BigDecimal parseAmountWithCode(String amountWithCode) {
		try {
			return new BigDecimal(amountWithCode.replace(",", ".").replace(" ", "").substring(0,
					amountWithCode.length() - 4));
		} catch (Exception e) {
			//Happens in downloaded csv when google sheet was loading currencies
			return BigDecimal.ZERO;
		}
	}

	public static boolean hasCSVFormat(MultipartFile file) {
		return TYPE.equals(file.getContentType());
	}

}
