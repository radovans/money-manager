package cz.sinko.moneymanager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.dto.CategoryStatisticsDto;
import cz.sinko.moneymanager.api.dto.IncomeExpenseStatementDto;
import cz.sinko.moneymanager.api.dto.LabelStatisticsDto;
import cz.sinko.moneymanager.api.dto.PeriodCategoryStatisticDto;
import cz.sinko.moneymanager.api.dto.PeriodCategoryStatisticsDto;
import cz.sinko.moneymanager.api.dto.RecipientStatisticDto;
import cz.sinko.moneymanager.api.dto.RecipientStatisticsDto;
import cz.sinko.moneymanager.api.dto.SpendingCategoriesDto;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StatisticsService {

	public static SpendingCategoriesDto calculateCategoriesStatistics(Map<Category, List<Transaction>> transactionsByCategories) {
		return calculateStatistics(transactionsByCategories, Category::getName);
	}

	public static SpendingCategoriesDto calculateSubcategoriesStatistics(Map<Subcategory, List<Transaction>> transactionsByCategories) {
		return calculateStatistics(transactionsByCategories, Subcategory::getName);
	}

	public static <T> SpendingCategoriesDto calculateStatistics(Map<T, List<Transaction>> transactionsByCategories, Function<T, String> nameFunction) {
		SpendingCategoriesDto response = new SpendingCategoriesDto();
		List<CategoryStatisticsDto> categoryDtos = transactionsByCategories.entrySet().stream().map(entry -> {
			CategoryStatisticsDto categoryDto = new CategoryStatisticsDto();
			categoryDto.setId(entry.getValue().stream().map(transaction -> transaction.getSubcategory().getId()).findFirst().orElse(null));
			categoryDto.setName(nameFunction.apply(entry.getKey()));
			BigDecimal amount = entry.getValue().stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add);
			categoryDto.setAmount(amount);
			categoryDto.setIsSubcategory(true);
			return categoryDto;
		}).filter(category -> category.getAmount().compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
		calculatePercentage(categoryDtos);
		response.setCategories(categoryDtos.stream().sorted(Comparator.comparing(CategoryStatisticsDto::getAmount)).collect(Collectors.toList()));
		return response;
	}

	public IncomeExpenseStatementDto createIncomeExpenseStatement(List<Transaction> transactions) {
		BigDecimal balance = calculateBalance(transactions);
		BigDecimal income = calculateIncome(transactions);
		BigDecimal expense = calculateExpense(transactions);
		return new IncomeExpenseStatementDto(balance, income, expense);
	}

	public List<PeriodCategoryStatisticsDto> createYearlyCategoryStatistics(List<Transaction> transactions) {
		return createCategoryStatistics(transactions, Transaction::getCategory, Category::getName, transaction -> Year.from(transaction.getDate()));
	}

	public List<PeriodCategoryStatisticsDto> createMonthlyCategoryStatistics(List<Transaction> transactions) {
		return createCategoryStatistics(transactions, Transaction::getCategory, Category::getName, transaction -> YearMonth.from(transaction.getDate()));
	}

	public List<PeriodCategoryStatisticsDto> createYearlySubcategoryStatistics(List<Transaction> transactions) {
		return createCategoryStatistics(transactions, Transaction::getSubcategory, Subcategory::getName, transaction -> Year.from(transaction.getDate()));
	}

	public List<PeriodCategoryStatisticsDto> createMonthlySubcategoryStatistics(List<Transaction> transactions) {
		return createCategoryStatistics(transactions, Transaction::getSubcategory, Subcategory::getName, transaction -> YearMonth.from(transaction.getDate()));
	}

	public List<RecipientStatisticsDto> createYearlyRecipientStatistics(List<Transaction> transactions) {
		return createRecipientStatistics(transactions, transaction -> Year.from(transaction.getDate()));
	}

	public List<RecipientStatisticsDto> createMonthlyRecipientStatistics(List<Transaction> transactions) {
		return createRecipientStatistics(transactions, transaction -> YearMonth.from(transaction.getDate()));
	}
	
	private <T, P> List<PeriodCategoryStatisticsDto> createCategoryStatistics(List<Transaction> transactions, Function<Transaction, T> groupingFunction, Function<T, String> nameFunction, Function<Transaction, P> periodFunction) {
		List<PeriodCategoryStatisticsDto> response = new ArrayList<>();
		Set<P> allPeriods = new HashSet<>();
		transactions.forEach(transaction -> allPeriods.add(periodFunction.apply(transaction)));
		Map<T, List<Transaction>> transactionsByCategories = transactions.stream().collect(Collectors.groupingBy(groupingFunction));
		transactionsByCategories.forEach((category, transactionsByCategory) -> {
			createPeriodCategoryStatistics(nameFunction, periodFunction, response, allPeriods, category, transactionsByCategory);
		});
		response.sort(Comparator.comparing(PeriodCategoryStatisticsDto::getTotalAmount));
		return response;
	}

	private static <T, P> void createPeriodCategoryStatistics(Function<T, String> nameFunction, Function<Transaction, P> periodFunction, List<PeriodCategoryStatisticsDto> response, Set<P> allPeriods, T category, List<Transaction> transactionsByCategory) {
		PeriodCategoryStatisticsDto periodCategoryStatisticsDto = new PeriodCategoryStatisticsDto();
		periodCategoryStatisticsDto.setCategory(nameFunction.apply(category));
		Map<P, List<Transaction>> transactionsByPeriod = transactionsByCategory.stream().collect(Collectors.groupingBy(periodFunction));
		allPeriods.forEach(period -> {
			createPeriodCategoryStatistic(periodCategoryStatisticsDto, transactionsByPeriod, period);
		});
		periodCategoryStatisticsDto.getPeriodCategoryStatistics().sort(Comparator.comparing(PeriodCategoryStatisticDto::getPeriod));
		createTotalPeriodCategoryStatistic(transactionsByCategory, periodCategoryStatisticsDto);
		response.add(periodCategoryStatisticsDto);
	}

	private static void createTotalPeriodCategoryStatistic(List<Transaction> transactionsByCategory, PeriodCategoryStatisticsDto periodCategoryStatisticsDto) {
		PeriodCategoryStatisticDto periodCategoryStatisticDto = new PeriodCategoryStatisticDto();
		periodCategoryStatisticDto.setPeriod("Total");
		BigDecimal totalAmount = transactionsByCategory.stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add);
		periodCategoryStatisticDto.setAmount(totalAmount);
		periodCategoryStatisticsDto.addPeriodCategoryStatisticDto(periodCategoryStatisticDto);
		periodCategoryStatisticsDto.setTotalAmount(totalAmount);
	}

	private static <P> void createPeriodCategoryStatistic(PeriodCategoryStatisticsDto periodCategoryStatisticsDto, Map<P, List<Transaction>> transactionsByPeriod, P period) {
		PeriodCategoryStatisticDto periodCategoryStatisticDto = new PeriodCategoryStatisticDto();
		periodCategoryStatisticDto.setPeriod(period.toString());
		periodCategoryStatisticDto.setAmount(transactionsByPeriod.getOrDefault(period, new ArrayList<>()).stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
		periodCategoryStatisticsDto.addPeriodCategoryStatisticDto(periodCategoryStatisticDto);
	}

	private static BigDecimal calculateExpense(List<Transaction> transactions) {
		return transactions.stream().map(Transaction::getAmountInCzk).filter(amountInCzk ->
				amountInCzk.compareTo(BigDecimal.ZERO)
						< 0).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private static BigDecimal calculateIncome(List<Transaction> transactions) {
		return transactions.stream().map(Transaction::getAmountInCzk).filter(amountInCzk ->
				amountInCzk.compareTo(BigDecimal.ZERO)
						> 0).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private static BigDecimal calculateBalance(List<Transaction> transactions) {
		return transactions.stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private static void calculatePercentage(List<CategoryStatisticsDto> categories) {
		BigDecimal totalAmount = categories.stream().map(CategoryStatisticsDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).abs();
		categories.forEach(category -> category.setPercentage(category.getAmount().abs().multiply(BigDecimal.valueOf(100)).divide(totalAmount, RoundingMode.HALF_UP)));
	}

	public List<LabelStatisticsDto> createLabelStatistics(List<Transaction> transactions) {
		// TODO implementation
		return null;
	}

	public <P> List<RecipientStatisticsDto> createRecipientStatistics(List<Transaction> transactions, Function<Transaction, P> periodFunction) {
		// TODO implementation
		return null;
	}

}
