package cz.sinko.moneymanager;

import static cz.sinko.moneymanager.api.response.IncomeExpenseStatementDto.CZK;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cz.sinko.moneymanager.api.response.PlannedTransactionDto;
import cz.sinko.moneymanager.api.response.RecurrentTransactionDto;
import cz.sinko.moneymanager.api.response.RuleDto;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.MainCategoryRepository;
import cz.sinko.moneymanager.repository.PlannedTransactionRepository;
import cz.sinko.moneymanager.repository.RecurrentTransactionRepository;
import cz.sinko.moneymanager.repository.RuleRepository;
import cz.sinko.moneymanager.repository.TransactionRepository;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.ExpenseType;
import cz.sinko.moneymanager.repository.model.Frequency;
import cz.sinko.moneymanager.repository.model.MainCategory;
import cz.sinko.moneymanager.repository.model.PlannedTransaction;
import cz.sinko.moneymanager.repository.model.RecurrentTransaction;
import cz.sinko.moneymanager.repository.model.Rule;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CsvUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class MoneyManagerApplication {

	public static final Gson GSON = new GsonBuilder().create();
	public static final String IMPORT_CSV_FILE = "C:\\Users\\radovan.sinko\\Downloads\\transactions.csv";
	public static final String CONFIGURATION_JSON = "C:\\Users\\radovan.sinko\\Downloads\\configuration.json";

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private MainCategoryRepository mainCategoryRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private PlannedTransactionRepository plannedTransactionRepository;

	@Autowired
	private RecurrentTransactionRepository recurrentTransactionRepository;

	@Autowired
	private RuleRepository ruleRepository;

	public static void main(String[] args) {
		SpringApplication.run(MoneyManagerApplication.class, args);
	}

	@PostConstruct
	private void postConstruct() {
		ConfigurationJson configurationJson = openConfiguration();
		setupAccounts(configurationJson.getAccounts());
		setupMainCategories(configurationJson.getMainCategories());
		setupCategories(configurationJson.getCategories());
		setupRules(configurationJson.getRules());
		setupPlannedTransactions(configurationJson.getPlannedTransactions());
		setupRecurrentTransactions(configurationJson.getRecurrentTransactions());
		List<String[]> transactions = CsvUtil.getRowsFromCsv(IMPORT_CSV_FILE, ',', StandardCharsets.UTF_8);
		importTransactions(transactions);
	}

	private void setupPlannedTransactions(List<PlannedTransactionDto> plannedTransactions) {
		plannedTransactions.forEach(plannedTransaction -> {
			PlannedTransaction plannedTransactionEntity = new PlannedTransaction();
			plannedTransactionEntity.setDayOfMonth(plannedTransaction.getDayOfMonth());
			plannedTransactionEntity.setRecipient(plannedTransaction.getRecipient());
			plannedTransactionEntity.setNote(plannedTransaction.getNote());
			plannedTransactionEntity.setAmount(plannedTransaction.getAmount());
			plannedTransactionEntity.setCurrency(plannedTransaction.getCurrency());
			plannedTransactionEntity.setMainCategory(mainCategoryRepository.findByName(plannedTransaction.getMainCategory()));
			plannedTransactionEntity.setCategory(categoryRepository.findByName(plannedTransaction.getCategory()));
			plannedTransactionEntity.setAccount(accountRepository.findByName(plannedTransaction.getAccount()));
			plannedTransactionEntity.setLabel(plannedTransaction.getLabel());
			plannedTransactionEntity.setExpenseType(plannedTransaction.getExpenseType() != null ?
					ExpenseType.valueOf(plannedTransaction.getExpenseType()) :
					null);
			plannedTransactionRepository.save(plannedTransactionEntity);
			log.info("Planned transaction saved '{}'", plannedTransactionEntity);
		});
	}

	private void setupRecurrentTransactions(List<RecurrentTransactionDto> recurrentTransactions) {
		recurrentTransactions.forEach(recurrentTransaction -> {
			RecurrentTransaction recurrentTransactionEntity = new RecurrentTransaction();
			recurrentTransactionEntity.setFirstPayment(MonthDay.parse(recurrentTransaction.getFirstPayment()));
			recurrentTransactionEntity.setFrequency(Frequency.valueOf(recurrentTransaction.getFrequency()));
			recurrentTransactionEntity.setRecipient(recurrentTransaction.getRecipient());
			recurrentTransactionEntity.setNote(recurrentTransaction.getNote());
			recurrentTransactionEntity.setAmount(recurrentTransaction.getAmount());
			recurrentTransactionEntity.setAmountInCzk(recurrentTransaction.getAmount());
			recurrentTransactionEntity.setCurrency(recurrentTransaction.getCurrency());
			recurrentTransactionEntity.setMainCategory(mainCategoryRepository.findByName(recurrentTransaction.getMainCategory()));
			recurrentTransactionEntity.setCategory(categoryRepository.findByName(recurrentTransaction.getCategory()));
			recurrentTransactionEntity.setAccount(accountRepository.findByName(recurrentTransaction.getAccount()));
			recurrentTransactionEntity.setLabel(recurrentTransaction.getLabel());
			recurrentTransactionEntity.setExpenseType(recurrentTransaction.getExpenseType() != null ?
					ExpenseType.valueOf(recurrentTransaction.getExpenseType()) :
					null);
			recurrentTransactionRepository.save(recurrentTransactionEntity);
			log.info("Recurrent transaction saved '{}'", recurrentTransactionEntity);
		});
	}

	private void setupRules(List<RuleDto> rules) {
		rules.forEach(rule -> {
			Rule ruleEntity = new Rule();
			ruleEntity.setKey(rule.getKey());
			ruleEntity.setType(rule.getType());
			ruleEntity.setRecipient(rule.getRecipient());
			ruleEntity.setNote(rule.getNote());
			ruleEntity.setMainCategory(mainCategoryRepository.findByName(rule.getMainCategory()));
			ruleEntity.setCategory(categoryRepository.findByName(rule.getCategory()));
			ruleEntity.setLabel(rule.getLabel());
			ruleRepository.save(ruleEntity);
			log.info("Rule saved '{}'", rule);
		});
	}

	public void setupAccounts(List<String> accounts) {
		accounts.forEach(account -> {
			Account accountEntity = new Account();
			accountEntity.setName(account);
			accountRepository.save(accountEntity);
			log.info("Account saved '{}'", accountEntity);
		});
	}

	public void setupMainCategories(List<String> mainCategories) {
		mainCategories.forEach(mainCategory -> {
			MainCategory mainCategoryEntity = new MainCategory();
			mainCategoryEntity.setName(mainCategory);
			mainCategoryRepository.save(mainCategoryEntity);
			log.info("Main cateory saved '{}'", mainCategoryEntity);
		});
	}

	public void setupCategories(Map<String, String> categories) {
		categories.forEach((category, mainCategory) -> {
			Category categoryEntity = new Category();
			categoryEntity.setName(category);
			categoryEntity.setMainCategory(mainCategoryRepository.findByName(mainCategory));
			categoryRepository.save(categoryEntity);
			log.info("Category saved '{}'", categoryEntity);
		});
	}

	private void importTransactions(List<String[]> transactions) {
		List<String[]> failedRows = new ArrayList<>();
		transactions.stream()
				.skip(1)
//				.limit(1)
				.forEach(transaction -> {
					try {
						Transaction transactionEntity = new Transaction();
						transactionEntity.setDate(LocalDate.parse(transaction[0], DateTimeFormatter.ofPattern("d.M.yyyy")));
						transactionEntity.setRecipient(transaction[1]);
						transactionEntity.setNote(transaction[2]);
						transactionEntity.setAmount(CsvUtil.parseAmount(transaction[3]));
						transactionEntity.setAmountInCzk(CsvUtil.parseAmountWithCode(transaction[4]));
						transactionEntity.setCurrency(transaction[5].isBlank() ? CZK : transaction[5]);
						transactionEntity.setMainCategory(mainCategoryRepository.findByName(transaction[6]));
						transactionEntity.setCategory(categoryRepository.findByName(transaction[7]));
						transactionEntity.setAccount(accountRepository.findByName(transaction[8]));
						transactionEntity.setLabel(transaction[9]);
						transactionRepository.save(transactionEntity);
						log.info("Transaction saved '{}'", transactionEntity);
					} catch (Exception e) {
						failedRows.add(transaction);
					}
				});
		if (!failedRows.isEmpty()) {
			failedRows.forEach(row -> log.error("Failed to save transaction '{}'", row));
		}
	}

	private static ConfigurationJson openConfiguration() {
		File file = new File(CONFIGURATION_JSON);
		try {
			return GSON.fromJson(Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8), ConfigurationJson.class);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open configuration file", e);
		}
	}

}

