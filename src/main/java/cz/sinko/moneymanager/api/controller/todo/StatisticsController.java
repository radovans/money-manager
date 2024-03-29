package cz.sinko.moneymanager.api.controller.todo;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.*;
import cz.sinko.moneymanager.connectors.service.ExchangeService;
import cz.sinko.moneymanager.connectors.service.dto.ExchangeRateDto;
import cz.sinko.moneymanager.facade.StatisticsFacade;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.StatisticsService;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
@AllArgsConstructor
@Slf4j
public class StatisticsController {

	// TODO call only facade, not services
	private final StatisticsFacade statisticsFacade;

	private final TransactionService transactionService;

	private final StatisticsService statisticsService;

	private final ExchangeService exchangeService;

	@GetMapping("/exchange-rates")
	public ResponseEntity<List<ExchangeRateDto>> getExchangeRates() {
		return ResponseEntity.ok().body(exchangeService.getExchangeRates());
	}

	@GetMapping("/exchange-rates/{date}")
	public ResponseEntity<ExchangeRateDto> getExchangeRates(@PathVariable LocalDate date) {
		return ResponseEntity.ok().body(exchangeService.getExchangeRate(date));
	}

	@GetMapping("/exchange-rates/convert/{date}/{currency}/{amount}")
	public ResponseEntity<BigDecimal> convertCurrency(@PathVariable LocalDate date, @PathVariable String currency,
													  @PathVariable BigDecimal amount) {
		return ResponseEntity.ok().body(exchangeService.convertCurrencyToCzk(Currency.getInstance(currency), date, amount));
	}

	@GetMapping("/categories/yearly")
	public ResponseEntity<List<PeriodCategoryStatisticsDto>> getYearlyCategoryStatistics() {
		return ResponseEntity.ok().body(statisticsFacade.getYearlyCategoryStatistics());
	}

	@GetMapping("/categories/monthly")
	public ResponseEntity<List<PeriodCategoryStatisticsDto>> getMonthlyCategoryStatistics() {
		return ResponseEntity.ok().body(statisticsFacade.getMonthlyCategoryStatistics());
	}

	@GetMapping("/subcategories/yearly")
	public ResponseEntity<List<PeriodCategoryStatisticsDto>> getYearlySubcategoryStatistics() {
		return ResponseEntity.ok().body(statisticsFacade.getYearlySubcategoryStatistics());
	}

	@GetMapping("/subcategories/monthly")
	public ResponseEntity<List<PeriodCategoryStatisticsDto>> getMonthlySubcategoryStatistics() {
		return ResponseEntity.ok().body(statisticsFacade.getMonthlySubcategoryStatistics());
	}

	@GetMapping("/recipients/yearly")
	public ResponseEntity<List<RecipientStatisticsDto>> getYearlyRecipientStatistics() {
		return ResponseEntity.ok().body(statisticsFacade.getYearlyRecipientStatistics());
	}

	@GetMapping("/recipients/monthly")
	public ResponseEntity<List<RecipientStatisticsDto>> getMonthlyRecipientStatistics() {
		return ResponseEntity.ok().body(statisticsFacade.getMonthlyRecipientStatistics());
	}

	@GetMapping("/labels")
	public ResponseEntity<List<LabelStatisticsDto>> getLabelStatistics() {
		return ResponseEntity.ok().body(statisticsFacade.getLabelStatistics());
	}

	// TODO clean up controller and move logic to service
	@GetMapping
	public ResponseEntity<StatisticsDto> getStatistics() {
		List<Transaction> transactions = transactionService.find();
		IncomeExpenseStatementDto incomeExpenseStatementDto = statisticsService.createIncomeExpenseStatement(transactions);

		List<Transaction> lastYearTransactions = transactionService.filterTransactionsByYear(transactions,
				Year.now().getValue() - 1);
		IncomeExpenseStatementDto lastYearIncomeExpenseStatementDto = statisticsService.createIncomeExpenseStatement(lastYearTransactions);

		List<Transaction> yearToDateTransactions = transactionService.filterTransactionsByYear(transactions, Year.now().getValue());
		IncomeExpenseStatementDto yearToDateIncomeExpenseStatementDto = statisticsService.createIncomeExpenseStatement(yearToDateTransactions);

		return ResponseEntity.ok().body(new StatisticsDto(incomeExpenseStatementDto, lastYearIncomeExpenseStatementDto, yearToDateIncomeExpenseStatementDto));
	}

	// TODO clean up controller and move logic to service
	@GetMapping("/categories")
	public ResponseEntity<SpendingCategoriesDto> getSpendingCategoriesStatistics(
			@RequestParam(defaultValue = "2020-01-01T00:00:00.000Z") String from,
			@RequestParam(defaultValue = "2023-12-31T23:59:59.999Z") String to,
			@RequestParam(required = false) String category)
			throws ResourceNotFoundException {
		if (category == null) {
			List<Transaction> transactions = transactionService.find(LocalDate.from(OffsetDateTime.parse(from)), LocalDate.from(OffsetDateTime.parse(to)));
			Map<Category, List<Transaction>> transactionsByCategories = transactions.stream().collect(Collectors.groupingBy(Transaction::getCategory));
			return ResponseEntity.ok().body(StatisticsService.calculateCategoriesStatistics(transactionsByCategories));
		} else {
			List<Transaction> transactions = transactionService.find(LocalDate.from(OffsetDateTime.parse(from)), LocalDate.from(OffsetDateTime.parse(to)), category);
			Map<Subcategory, List<Transaction>> transactionsBySubcategories = transactions.stream().collect(Collectors.groupingBy(Transaction::getSubcategory));
			return ResponseEntity.ok().body(StatisticsService.calculateSubcategoriesStatistics(transactionsBySubcategories));
		}
	}

	// TODO clean up controller and move logic to service
	@GetMapping("/year-month/all")
	public ResponseEntity<List<MonthStatisticDto>> getYearMonthStatistics(
			@RequestParam(defaultValue = "2020-01") String from,
			@RequestParam(defaultValue = "2023-12") String to) {
		List<Transaction> transactions = transactionService.find(YearMonth.parse(from), YearMonth.parse(to));
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
		response.forEach(monthStatisticDto -> monthStatisticDto.setCumulativeBalance(response.stream()
				.filter(monthStatistic -> monthStatistic.getMonth().isBefore(monthStatisticDto.getMonth()))
				.map(MonthStatisticDto::getBalance)
				.reduce(BigDecimal.ZERO, BigDecimal::add).add(monthStatisticDto.getBalance())));
		return ResponseEntity.ok().body(response);
	}

	// TODO clean up controller and move logic to service, separate to smaller methods
	@GetMapping("/year-month/year")
	public ResponseEntity<Map<String, List<MonthStatisticDto>>> getYearMonthStatisticsByYear(
			@RequestParam(defaultValue = "2020-01") String from,
			@RequestParam(defaultValue = "2023-12") String to,
			@RequestParam(defaultValue = "false") boolean salaryOnly)
			throws ResourceNotFoundException {
		List<Transaction> transactions;
		if (salaryOnly) {
			transactions = transactionService.find(YearMonth.parse(from), YearMonth.parse(to), "Príjem");
		} else {
			transactions = transactionService.find(YearMonth.parse(from), YearMonth.parse(to));
		}
		Map<String, List<MonthStatisticDto>> response = new HashMap<>();
		Map<Year, List<Transaction>> transactionsByYear = transactions.stream().collect(Collectors.groupingBy(transaction -> Year.from(transaction.getDate())));
		transactionsByYear.entrySet().forEach(yearListEntry -> {
					List<MonthStatisticDto> monthStatistics = new ArrayList<>();
					Map<YearMonth, List<Transaction>> transactionsByYearMonth = yearListEntry.getValue().stream().collect(Collectors.groupingBy(transaction -> YearMonth.from(transaction.getDate())));
					transactionsByYearMonth.entrySet().forEach(yearMonthListEntry -> {
						MonthStatisticDto monthStatisticDto = new MonthStatisticDto();
						monthStatisticDto.setMonth(yearMonthListEntry.getKey());
						BigDecimal incomes = BigDecimal.valueOf(yearMonthListEntry.getValue().stream().filter(transaction ->
								transaction.getAmountInCzk().doubleValue()
										> 0).mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue()).sum()).setScale(2, RoundingMode.HALF_UP);
						BigDecimal expenses = BigDecimal.valueOf(yearMonthListEntry.getValue().stream().filter(transaction ->
								transaction.getAmountInCzk().doubleValue()
										< 0).mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue()).sum()).setScale(2, RoundingMode.HALF_UP);
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
			monthStatisticDto.setExpenses(BigDecimal.valueOf(monthListEntry.getValue().stream().filter(transaction ->
					transaction.getAmountInCzk().doubleValue()
							< 0).mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue()).sum()).setScale(2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(monthListEntry.getValue().stream().map(Transaction::getDate).map(date -> Year.from(date).getValue()).distinct().count()), RoundingMode.HALF_UP));
			monthStatistics.add(monthStatisticDto);
		});
		monthStatistics.sort(Comparator.comparing(MonthStatisticDto::getMonth));
		response.put("xAverage", monthStatistics);
		return ResponseEntity.ok().body(response);
	}

}
