package cz.sinko.moneymanager.api.controller.todo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.facade.ImportFacade;
import cz.sinko.moneymanager.facade.RuleFacade;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CsvUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/process")
@AllArgsConstructor
@Slf4j
public class ProcessController {

	private static final String AIRBANK_CSV_FILENAME = "airbank-export.csv";
	private static final String CSOB_CSV_FILENAME = "csob-export.csv";
	private static final String CSOB_KREDIT_CSV_FILENAME = "csob-kredit-export.csv";
	private static final String KB_CSV_FILENAME = "kb-export.csv";
	private static final String UNICREDIT_CSV_FILENAME = "unicredit-export.csv";

	@Autowired
	private ImportFacade importFacade;

	@Autowired
	private RuleFacade ruleFacade;

	@PostMapping(value = "/airbank", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processAirbankCsv(@RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException {
		return processCsvFile(file, importFacade.airbankParseTransactions(file), AIRBANK_CSV_FILENAME);
	}

	@PostMapping(value = "/csob", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processCsobCsv(@RequestParam("file") MultipartFile file) throws ResourceNotFoundException {
		return processCsvFile(file, importFacade.csobParseTransactions(file), CSOB_CSV_FILENAME);
	}

	@PostMapping(value = "/csobKredit", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processCsobKreditCsv(@RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException {
		return processCsvFile(file, importFacade.csobKreditParseTransactions(file), CSOB_KREDIT_CSV_FILENAME);
	}

	@PostMapping(value = "/kb", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processKbCsv(@RequestParam("file") MultipartFile file) throws ResourceNotFoundException {
		return processCsvFile(file, importFacade.kbParseTransactions(file), KB_CSV_FILENAME);
	}

	@PostMapping(value = "/unicredit", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processUnicreditCsv(@RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException {
		return processCsvFile(file, importFacade.unicreditParseTransactions(file), UNICREDIT_CSV_FILENAME);
	}

	private ResponseEntity<?> processCsvFile(@RequestParam("file") MultipartFile file, List<Transaction> transactions, String csvFilename) {
		String message;

		if (CsvUtil.hasCSVFormat(file)) {
			try {
				log.info("Processing csv file with transactions.");
				List<Transaction> processedTransactions = ruleFacade.applyRules(transactions);
				log.info("File processed successfully: " + file.getOriginalFilename());

				return createCsvFile(processedTransactions, csvFilename);
			} catch (Exception e) {
				message = "Could not process file: " + file.getOriginalFilename() + "!";
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
			}
		} else {
			message = "File is not in csv format: " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
		}
	}

	private ResponseEntity<?> createCsvFile(List<Transaction> transactions, String csvFilename) {
		InputStreamResource output = CsvUtil.createCsvOutput(transactions);
		return ResponseEntity
				.status(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFilename)
				.header(HttpHeaders.CONTENT_TYPE, "text/csv")
				.body(output);
	}
}