package cz.sinko.moneymanager;

import static java.util.Collections.singleton;
import static org.zalando.logbook.BodyFilter.merge;
import static org.zalando.logbook.BodyFilters.defaultValue;
import static org.zalando.logbook.Conditions.exclude;
import static org.zalando.logbook.Conditions.requestTo;
import static org.zalando.logbook.json.JsonBodyFilters.replaceJsonStringProperty;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonHttpLogFormatter;
import org.zalando.logbook.logstash.LogstashLogbackSink;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.PlannedTransactionDto;
import cz.sinko.moneymanager.api.dto.RecurrentTransactionDto;
import cz.sinko.moneymanager.api.dto.RuleDto;
import cz.sinko.moneymanager.config.ConfigurationJson;
import cz.sinko.moneymanager.repository.AccountRepository;
import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.PlannedTransactionRepository;
import cz.sinko.moneymanager.repository.RecurrentTransactionRepository;
import cz.sinko.moneymanager.repository.RuleRepository;
import cz.sinko.moneymanager.repository.SubcategoryRepository;
import cz.sinko.moneymanager.repository.TransactionRepository;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.ExpenseType;
import cz.sinko.moneymanager.repository.model.Frequency;
import cz.sinko.moneymanager.repository.model.PlannedTransaction;
import cz.sinko.moneymanager.repository.model.RecurrentTransaction;
import cz.sinko.moneymanager.repository.model.Rule;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CategoryService;
import cz.sinko.moneymanager.service.CsvUtil;
import cz.sinko.moneymanager.service.SubcategoryService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@AllArgsConstructor
public class MoneyManagerApplication {

	public static final Gson GSON = new GsonBuilder().create();
	//		public static final String IMPORT_CSV_FILE = "C:\\Users\\radovan.sinko\\Downloads\\transactions.csv";
	//		public static final String CONFIGURATION_JSON = "C:\\Users\\radovan.sinko\\Downloads\\configuration.json";
	public static final String IMPORT_CSV_FILE = ".\\src\\main\\resources\\transactions - example.csv";
	public static final String CONFIGURATION_JSON = ".\\src\\main\\resources\\configuration-example.json";

	private AccountRepository accountRepository;

	private CategoryService categoryService;

	private SubcategoryService subcategoryService;

	private TransactionRepository transactionRepository;

	private PlannedTransactionRepository plannedTransactionRepository;

	private RecurrentTransactionRepository recurrentTransactionRepository;

	private CategoryRepository categoryRepository;

	private SubcategoryRepository subcategoryRepository;

	private RuleRepository ruleRepository;

	public static void main(String[] args) {
		SpringApplication.run(MoneyManagerApplication.class, args);
		log.info("Application: http://localhost:8088/actuator/health");
		log.info("Swagger doc: http://localhost:8088/swagger");
		log.info("Redis Insight: http://localhost:8001/");
	}

	private static ConfigurationJson openConfiguration() {
		File file = new File(CONFIGURATION_JSON);
		try {
			return GSON.fromJson(Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8), ConfigurationJson.class);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open configuration file", e);
		}
	}

	@PostConstruct
	private void postConstruct() {
		ConfigurationJson configurationJson = openConfiguration();
		setupAccounts(configurationJson.getAccounts());
		setupCategories(configurationJson.getCategories());
		setupSubcategories(configurationJson.getSubcategories());
		setupRules(configurationJson.getRules());
		setupPlannedTransactions(configurationJson.getPlannedTransactions());
		setupRecurrentTransactions(configurationJson.getRecurrentTransactions());
		List<String[]> transactions = CsvUtil.getRowsFromCsv(IMPORT_CSV_FILE, ',', StandardCharsets.UTF_8);
		importTransactions(transactions);
	}

	private void setupPlannedTransactions(List<PlannedTransactionDto> plannedTransactions) {
		plannedTransactions.forEach(plannedTransaction -> {
			try {
				PlannedTransaction plannedTransactionEntity = new PlannedTransaction();
				plannedTransactionEntity.setDayOfMonth(plannedTransaction.getDayOfMonth());
				plannedTransactionEntity.setRecipient(plannedTransaction.getRecipient());
				plannedTransactionEntity.setNote(plannedTransaction.getNote());
				plannedTransactionEntity.setAmount(plannedTransaction.getAmount());
				plannedTransactionEntity.setCurrency(plannedTransaction.getCurrency());
				plannedTransactionEntity.setCategory(categoryService.find(plannedTransaction.getCategory()));
				plannedTransactionEntity.setSubcategory(subcategoryService.find(plannedTransaction.getSubcategory()));
				plannedTransactionEntity.setAccount(accountRepository.findByName(plannedTransaction.getAccount()).get());
				plannedTransactionEntity.setLabel(plannedTransaction.getLabel());
				plannedTransactionEntity.setExpenseType(plannedTransaction.getExpenseType() != null ?
						ExpenseType.valueOf(plannedTransaction.getExpenseType()) :
						null);
				plannedTransactionRepository.save(plannedTransactionEntity);
				log.info("Planned transaction saved '{}'", plannedTransactionEntity);
			} catch (ResourceNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void setupRecurrentTransactions(List<RecurrentTransactionDto> recurrentTransactions) {
		recurrentTransactions.forEach(recurrentTransaction -> {
			try {
				RecurrentTransaction recurrentTransactionEntity = new RecurrentTransaction();
				recurrentTransactionEntity.setFirstPayment(MonthDay.parse(recurrentTransaction.getFirstPayment()));
				recurrentTransactionEntity.setFrequency(Frequency.valueOf(recurrentTransaction.getFrequency()));
				recurrentTransactionEntity.setRecipient(recurrentTransaction.getRecipient());
				recurrentTransactionEntity.setNote(recurrentTransaction.getNote());
				recurrentTransactionEntity.setAmount(recurrentTransaction.getAmount());
				recurrentTransactionEntity.setAmountInCzk(recurrentTransaction.getAmount());
				recurrentTransactionEntity.setCurrency(recurrentTransaction.getCurrency());
				recurrentTransactionEntity.setCategory(categoryService.find(recurrentTransaction.getCategory()));
				recurrentTransactionEntity.setSubcategory(subcategoryService.find(recurrentTransaction.getSubcategory()));
				recurrentTransactionEntity.setAccount(accountRepository.findByName(recurrentTransaction.getAccount()).get());
				recurrentTransactionEntity.setLabel(recurrentTransaction.getLabel());
				recurrentTransactionEntity.setExpenseType(recurrentTransaction.getExpenseType() != null ?
						ExpenseType.valueOf(recurrentTransaction.getExpenseType()) :
						null);
				recurrentTransactionRepository.save(recurrentTransactionEntity);
				log.info("Recurrent transaction saved '{}'", recurrentTransactionEntity);
			} catch (ResourceNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void setupRules(List<RuleDto> rules) {
		rules.forEach(rule -> {
			try {
				Rule ruleEntity = new Rule();
				ruleEntity.setKey(rule.getKey());
				ruleEntity.setType(rule.getType());
				ruleEntity.setSkipTransaction(rule.isSkipTransaction());
				ruleEntity.setRecipient(rule.getRecipient());
				ruleEntity.setNote(rule.getNote());
				if (!rule.isSkipTransaction()) {
					if (rule.getCategory() != null) {
						ruleEntity.setCategory(categoryService.find(rule.getCategory()));
					}
					if (rule.getSubcategory() != null) {
						ruleEntity.setSubcategory(subcategoryService.find(rule.getSubcategory()));
					}
				}
				ruleEntity.setLabel(rule.getLabel());
				ruleRepository.save(ruleEntity);
				log.info("Rule saved '{}'", rule);
			} catch (ResourceNotFoundException e) {
				throw new RuntimeException(e);
			}
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

	public void setupCategories(List<String> categories) {
		categories.forEach(category -> {
			Category categoryEntity = new Category();
			categoryEntity.setName(category);
			categoryRepository.save(categoryEntity);
			log.info("Main category saved '{}'", categoryEntity);
		});
	}

	public void setupSubcategories(Map<String, String> subcategories) {
		subcategories.forEach((subcategory, category) -> {
			try {
				Subcategory subcategoryEntity = new Subcategory();
				subcategoryEntity.setName(subcategory);
				subcategoryEntity.setCategory(categoryService.find(category));
				subcategoryRepository.save(subcategoryEntity);
				log.info("Category saved '{}'", subcategoryEntity);
			} catch (ResourceNotFoundException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void importTransactions(List<String[]> transactions) {
		List<String[]> failedRows = new ArrayList<>();
		transactions.stream()
				.skip(1)
//				.limit(10)
				.forEach(transaction -> {
					try {
						Transaction transactionEntity = new Transaction();
						transactionEntity.setDate(LocalDate.parse(transaction[0], DateTimeFormatter.ofPattern("d.M.yyyy")));
						transactionEntity.setRecipient(transaction[1]);
						transactionEntity.setNote(transaction[2]);
						transactionEntity.setAmount(CsvUtil.parseAmount(transaction[3]));
						transactionEntity.setAmountInCzk(CsvUtil.parseAmountWithCode(transaction[4]));
						transactionEntity.setCurrency(transaction[5].isBlank() ? "CZK" : transaction[5]);
						transactionEntity.setCategory(categoryService.find(transaction[6]));
						transactionEntity.setSubcategory(subcategoryService.find(transaction[7]));
						transactionEntity.setAccount(accountRepository.findByName(transaction[8]).get());
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

	@Bean
	public Logbook logbook() {
		HttpLogFormatter formatter = new JsonHttpLogFormatter();
		LogstashLogbackSink sink = new LogstashLogbackSink(formatter);

		return Logbook.builder()
				.sink(sink)
				.condition(exclude(
						requestTo("/actuator"),
						requestTo("/actuator/*"),
						requestTo("/v3/api-docs"),
						requestTo("/swagger-ui.html"),
						requestTo("/swagger-resources"),
						requestTo("/csrf"),
						requestTo("/webjars/springfox-swagger-ui")))
//				.bodyFilter(merge(defaultValue(),
//						replaceJsonStringProperty(singleton("password"), "hidden-value")))
				.build();
	}

}

