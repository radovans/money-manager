package cz.sinko.moneymanager.api.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.RequestValidationException;
import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.response.SpendingCategoriesDto;
import cz.sinko.moneymanager.api.response.IncomeExpenseStatementDto;
import cz.sinko.moneymanager.api.response.MonthStatisticDto;
import cz.sinko.moneymanager.api.response.StatisticsDto;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.StatisticsService;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/statistics")
@AllArgsConstructor
@Slf4j
public class StatisticsController {

	private final TransactionService transactionService;

	private final StatisticsService statisticsService;

	@GetMapping
	public StatisticsDto getStatistics() {
		List<Transaction> transactions = transactionService.findTransactions();
		IncomeExpenseStatementDto incomeExpenseStatementDto = statisticsService.createIncomeExpenseStatement(transactions);

		List<Transaction> lastYearTransactions = transactionService.filterTransactionsByYear(transactions,
				Year.now().getValue() - 1);
		IncomeExpenseStatementDto lastYearIncomeExpenseStatementDto = statisticsService.createIncomeExpenseStatement(lastYearTransactions);

		List<Transaction> yearToDateTransactions = transactionService.filterTransactionsByYear(transactions, Year.now().getValue());
		IncomeExpenseStatementDto yearToDateIncomeExpenseStatementDto = statisticsService.createIncomeExpenseStatement(yearToDateTransactions);

		return new StatisticsDto(incomeExpenseStatementDto, lastYearIncomeExpenseStatementDto, yearToDateIncomeExpenseStatementDto);
	}

	@GetMapping("/categories")
	public SpendingCategoriesDto getSpendingCategoriesStatistics(
			@RequestParam(defaultValue = "2020-01-01T00:00:00.000Z") String from,
			@RequestParam(defaultValue = "2023-12-31T23:59:59.999Z") String to,
			@RequestParam(required = false) String category)
			throws RequestValidationException, ResourceNotFoundException {
		validateRequest(from, to);
		if (category == null) {
			List<Transaction> transactions = transactionService.findTransactions(LocalDate.from(OffsetDateTime.parse(from)), LocalDate.from(OffsetDateTime.parse(to)));
			Map<Category, List<Transaction>> transactionsByCategories = transactions.stream().collect(Collectors.groupingBy(Transaction::getCategory));
			return StatisticsService.calculateCategoriesStatistics(transactionsByCategories);
		} else {
			List<Transaction> transactions = transactionService.findTransactions(LocalDate.from(OffsetDateTime.parse(from)), LocalDate.from(OffsetDateTime.parse(to)), category);
			Map<Subcategory, List<Transaction>> transactionsBySubcategories = transactions.stream().collect(Collectors.groupingBy(Transaction::getSubcategory));
			return StatisticsService.calculateSubcategoriesStatistics(transactionsBySubcategories);
		}
	}

	@GetMapping("/year-month/all")
	public List<MonthStatisticDto> getYearMonthStatistics(
			@RequestParam(defaultValue = "2020-01") String from,
			@RequestParam(defaultValue = "2023-12") String to) throws RequestValidationException {
		validateYearMonthRequest(from, to);
		List<Transaction> transactions = transactionService.findTransactions(YearMonth.parse(from), YearMonth.parse(to));
		Map<YearMonth, Double> transactionsByMonth = transactions.stream()
				.collect(Collectors.groupingBy(transaction -> YearMonth.from(transaction.getDate()),
						TreeMap::new,
						Collectors.summingDouble(transaction -> transaction.getAmountInCzk().doubleValue())));
		List<MonthStatisticDto> response = new ArrayList<>();
		transactionsByMonth.forEach((month, amount) -> {
			MonthStatisticDto monthStatisticDto = new MonthStatisticDto();
			monthStatisticDto.setMonth(month);
			monthStatisticDto.setBalance(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP));
			response.add(monthStatisticDto);
		});
		response.forEach(monthStatisticDto -> monthStatisticDto.setCumulativeAmount(response.stream()
				.filter(monthStatistic -> monthStatistic.getMonth().isBefore(monthStatisticDto.getMonth()))
				.map(MonthStatisticDto::getBalance)
				.reduce(BigDecimal.ZERO, BigDecimal::add).add(monthStatisticDto.getBalance())));
		return response;
	}

	@GetMapping("/year-month/year")
	public Map<String, List<MonthStatisticDto>> getYearMonthStatisticsByYear(
			@RequestParam(defaultValue = "2020-01") String from,
			@RequestParam(defaultValue = "2023-12") String to,
			@RequestParam(defaultValue = "false") boolean salaryOnly)
			throws RequestValidationException, ResourceNotFoundException {
		validateYearMonthRequest(from, to);
		List<Transaction> transactions;
		if (salaryOnly) {
			transactions = transactionService.findTransactions(YearMonth.parse(from), YearMonth.parse(to), "Príjem");
		} else {
			transactions = transactionService.findTransactions(YearMonth.parse(from), YearMonth.parse(to));
		}
		Map<String, List<MonthStatisticDto>> response = new HashMap<>();
		Map<Year, List<Transaction>> transactionsByYear = transactions.stream().collect(Collectors.groupingBy(transaction -> Year.from(transaction.getDate())));
		transactionsByYear.entrySet().forEach(yearListEntry -> {
					List<MonthStatisticDto> monthStatistics = new ArrayList<>();
					Map<YearMonth, List<Transaction>> transactionsByYearMonth = yearListEntry.getValue().stream().collect(Collectors.groupingBy(transaction -> YearMonth.from(transaction.getDate())));
					transactionsByYearMonth.entrySet().forEach(yearMonthListEntry -> {
						MonthStatisticDto monthStatisticDto = new MonthStatisticDto();
						monthStatisticDto.setMonth(yearMonthListEntry.getKey());
						BigDecimal incomes = BigDecimal.valueOf(yearMonthListEntry.getValue().stream().filter(transaction -> transaction.getAmountInCzk().doubleValue() > 0).mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue()).sum()).setScale(2, RoundingMode.HALF_UP);
						BigDecimal expenses = BigDecimal.valueOf(yearMonthListEntry.getValue().stream().filter(transaction -> transaction.getAmountInCzk().doubleValue() < 0).mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue()).sum()).setScale(2, RoundingMode.HALF_UP);
						monthStatisticDto.setIncomes(incomes);
						monthStatisticDto.setExpenses(expenses);
						monthStatisticDto.setBalance(incomes.add(expenses));
						monthStatistics.add(monthStatisticDto);
					});
					monthStatistics.sort(Comparator.comparing(MonthStatisticDto::getMonth));
					response.put("x" + yearListEntry.getKey().getValue(), monthStatistics);
				}
		);

		Map<Month, List<Transaction>> transactionsByMonth = transactions.stream().collect(Collectors.groupingBy(transaction -> Month.from(transaction.getDate())));
		List<MonthStatisticDto> monthStatistics = new ArrayList<>();
		transactionsByMonth.entrySet().forEach(monthListEntry -> {
			MonthStatisticDto monthStatisticDto = new MonthStatisticDto();
			monthStatisticDto.setMonth(YearMonth.of(1900, monthListEntry.getKey()));
			monthStatisticDto.setBalance(BigDecimal.valueOf(monthListEntry.getValue().stream().mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue()).sum()).setScale(2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(monthListEntry.getValue().stream().map(Transaction::getDate).map(date -> Year.from(date).getValue()).distinct().count()), RoundingMode.HALF_UP));
			monthStatisticDto.setExpenses(BigDecimal.valueOf(monthListEntry.getValue().stream().filter(transaction -> transaction.getAmountInCzk().doubleValue() < 0).mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue()).sum()).setScale(2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(monthListEntry.getValue().stream().map(Transaction::getDate).map(date -> Year.from(date).getValue()).distinct().count()), RoundingMode.HALF_UP));
			monthStatistics.add(monthStatisticDto);
		});
		monthStatistics.sort(Comparator.comparing(MonthStatisticDto::getMonth));
		response.put("xAverage", monthStatistics);
		return response;
	}

	private void validateRequest(String from, String to) throws RequestValidationException {
		List<ObjectError> errors = new ArrayList<>();
		ValidationUtils.validateOffsetDateTimeFormat(from, errors, "from");
		ValidationUtils.validateOffsetDateTimeFormat(to, errors, "to");
		ValidationUtils.checkErrors(errors);
	}

	private void validateYearMonthRequest(String from, String to) throws RequestValidationException {
		List<ObjectError> errors = new ArrayList<>();
		ValidationUtils.validateYearMonthFormat(from, errors, "from");
		ValidationUtils.validateYearMonthFormat(to, errors, "to");
		ValidationUtils.checkErrors(errors);
	}

}
