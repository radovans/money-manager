package cz.sinko.moneymanager.api.controller;

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

import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CsvUtil;
import cz.sinko.moneymanager.service.RuleService;
import cz.sinko.moneymanager.service.imports.AirbankImportService;
import cz.sinko.moneymanager.service.imports.CSOBImportService;
import cz.sinko.moneymanager.service.imports.CSOBKreditImportService;
import cz.sinko.moneymanager.service.imports.KBImportService;
import cz.sinko.moneymanager.service.imports.UnicreditImportService;
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
	private AirbankImportService airbankImport;

	@Autowired
	private CSOBImportService csobImport;

	@Autowired
	private CSOBKreditImportService csobKreditImport;

	@Autowired
	private KBImportService kbImport;

	@Autowired
	private UnicreditImportService unicreditImport;

	@Autowired
	private RuleService rulesService;

	@PostMapping(value = "/airbank", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processAirbankCsv(@RequestParam("file") MultipartFile file) {
		return processCsvFile(file, airbankImport.parseTransactions(file), AIRBANK_CSV_FILENAME);
	}

	@PostMapping(value = "/csob", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processCsobCsv(@RequestParam("file") MultipartFile file) {
		return processCsvFile(file, csobImport.parseTransactions(file), CSOB_CSV_FILENAME);
	}

	@PostMapping(value = "/csobKredit", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processCsobKreditCsv(@RequestParam("file") MultipartFile file) {
		return processCsvFile(file, csobKreditImport.parseTransactions(file), CSOB_KREDIT_CSV_FILENAME);
	}

	@PostMapping(value = "/kb", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processKbCsv(@RequestParam("file") MultipartFile file) {
		return processCsvFile(file, kbImport.parseTransactions(file), KB_CSV_FILENAME);
	}

	@PostMapping(value = "/unicredit", consumes = "multipart/form-data", produces = "text/csv")
	public ResponseEntity<?> processUnicreditCsv(@RequestParam("file") MultipartFile file) {
		return processCsvFile(file, unicreditImport.parseTransactions(file), UNICREDIT_CSV_FILENAME);
	}

	private ResponseEntity<?> processCsvFile(@RequestParam("file") MultipartFile file, List<Transaction> transactions, String csvFilename) {
		String message;

		if (CsvUtil.hasCSVFormat(file)) {
			try {
				log.info("Processing csv file with transactions.");
				List<Transaction> processedTransactions = rulesService.applyRules(transactions);
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