package cz.sinko.moneymanager.facade;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.AccountService;
import cz.sinko.moneymanager.service.ImportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ImportFacade {

	public static final char SEPARATOR = ';';
	public static final String DOT_DATE_PATTERN = "dd.MM.yyyy";
	public static final String DASH_DATE_PATTERN = "yyyy-MM-dd";
	public static final String SLASH_DATE_PATTERN = "dd/MM/yyyy";
	public static final String CP_1250 = "CP1250";
	public static final String EUR = "EUR";
	public static final String CSOB = "ČSOB";
	public static final String UNICREDIT = "Unicredit";
	public static final String KOMERCNI_BANKA = "Komerční banka";
	public static final String CSOB_KREDIT = "ČSOB Kredit";
	public static final String AIR_BANK = "AirBank";

	private AccountService accountService;

	private ImportService importService;

	public List<Transaction> csobParseTransactions(MultipartFile file) throws ResourceNotFoundException {
		Account account = accountService.find(CSOB);
		return importService.parseTransactions(file, SEPARATOR, StandardCharsets.UTF_8, account, 3, DOT_DATE_PATTERN, transaction -> transaction[1], transaction -> transaction[7], transaction ->
				transaction[14] + " - " + transaction[11], transaction -> transaction[2], null);
	}

	public List<Transaction> unicreditParseTransactions(MultipartFile file) throws ResourceNotFoundException {
		Account account = accountService.find(UNICREDIT);
		return importService.parseTransactions(file, SEPARATOR, StandardCharsets.UTF_8, account, 4, DASH_DATE_PATTERN, transaction -> transaction[3], transaction -> transaction[9], transaction -> transaction[13], transaction -> transaction[1], EUR);
	}

	public List<Transaction> kbParseTransactions(MultipartFile file) throws ResourceNotFoundException {
		Account account = accountService.find(KOMERCNI_BANKA);
		return importService.parseTransactions(file, SEPARATOR, Charset.forName(CP_1250), account, 18, DOT_DATE_PATTERN, transaction -> transaction[0], transaction -> transaction[15], transaction -> transaction[13], transaction -> transaction[4], null);
	}

	public List<Transaction> csobKreditParseTransactions(MultipartFile file) throws ResourceNotFoundException {
		Account account = accountService.find(CSOB_KREDIT);
		return importService.parseTransactions(file, SEPARATOR, StandardCharsets.UTF_8, account, 3, DOT_DATE_PATTERN, transaction -> transaction[1], transaction -> transaction[7], transaction ->
				transaction[14] + " - " + transaction[11], transaction -> transaction[2], null);
	}

	public List<Transaction> airbankParseTransactions(MultipartFile file) throws ResourceNotFoundException {
		Account account = accountService.find(AIR_BANK);
		return importService.parseTransactions(file, SEPARATOR, Charset.forName(CP_1250), account, 1, SLASH_DATE_PATTERN, transaction -> transaction[0], transaction -> transaction[9], transaction -> transaction[19], transaction -> transaction[5], null);
	}

}
