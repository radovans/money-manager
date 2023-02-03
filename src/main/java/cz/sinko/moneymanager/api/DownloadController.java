package cz.sinko.moneymanager.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.CSVWriter;

import cz.sinko.moneymanager.model.Transaction;
import cz.sinko.moneymanager.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/download")
@AllArgsConstructor
@Slf4j
public class DownloadController {

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
	public static final String EXPORT_CSV_FILE = "C:\\Users\\radovan.sinko\\Downloads\\transactions-export.csv";
	@Autowired
	private TransactionRepository transactionRepository;

	@GetMapping(value = "/transactions/csv")
	public ResponseEntity<?> downloadTransactions() throws Exception {
		log.info("Creating csv file with transactions.");
		List<Transaction> transactions = transactionRepository.findAll();
		createCsvFile(transactions);
		return ResponseEntity.ok().build();
	}

	public static void createCsvFile(List<Transaction> transactions) {
		File file = new File(EXPORT_CSV_FILE);
		try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {

			String[] header = { DATE, RECIPIENT, NOTE, AMOUNT, AMOUNT_IN_CZK, CURRENCY, MAIN_CATEGORY,
					CATEGORY, ACCOUNT, LABEL };
			writer.writeNext(header);

			for (Transaction transaction : transactions) {
				String[] data = { transaction.getDate().format(DateTimeFormatter.ofPattern("d.M.yyyy")),
						transaction.getRecipient(),
						transaction.getNote(),
						transaction.getAmount().toString().replace(".", ","),
						transaction.getAmountInCzk().toString().replace(".", ",") + " Kč",
						transaction.getCurrency(),
						transaction.getMainCategory().getName(),
						transaction.getCategory().getName(),
						transaction.getAccount().getName(),
						transaction.getLabel() };
				writer.writeNext(data);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
