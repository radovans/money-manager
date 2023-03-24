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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StatisticsService {

	public static SpendingCategoriesDto calculateCategoriesStatistics(Map<Category, List<Transaction>> transactionsByCategories) {
		SpendingCategoriesDto response = new SpendingCategoriesDto();
		List<CategoryStatisticsDto> categoryDtos = transactionsByCategories.entrySet().stream().map(entry -> {
			CategoryStatisticsDto categoryDto = new CategoryStatisticsDto();
			categoryDto.setId(entry.getValue().stream().map(transaction -> transaction.getCategory().getId()).findFirst().orElse(null));
			categoryDto.setName(entry.getKey().getName());
			BigDecimal amount = entry.getValue().stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add);
			categoryDto.setAmount(amount);
			categoryDto.setIsSubcategory(false);
			return categoryDto;
		}).filter(category -> category.getAmount().compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
		calculatePercentage(categoryDtos);
		response.setCategories(categoryDtos.stream().sorted(Comparator.comparing(CategoryStatisticsDto::getAmount)).collect(Collectors.toList()));
		response.setTotal(categoryDtos.stream().map(CategoryStatisticsDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
		return response;
	}

	public static SpendingCategoriesDto calculateSubcategoriesStatistics(Map<Subcategory, List<Transaction>> transactionsByCategories) {
		SpendingCategoriesDto response = new SpendingCategoriesDto();
		List<CategoryStatisticsDto> categoryDtos = transactionsByCategories.entrySet().stream().map(entry -> {
			CategoryStatisticsDto categoryDto = new CategoryStatisticsDto();
			categoryDto.setId(entry.getValue().stream().map(transaction -> transaction.getSubcategory().getId()).findFirst().orElse(null));
			categoryDto.setName(entry.getKey().getName());
			BigDecimal amount = entry.getValue().stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add);
			categoryDto.setAmount(amount);
			categoryDto.setIsSubcategory(true);
			return categoryDto;
		}).filter(category -> category.getAmount().compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
		calculatePercentage(categoryDtos);
		response.setCategories(categoryDtos.stream().sorted(Comparator.comparing(CategoryStatisticsDto::getAmount)).collect(Collectors.toList()));
		return response;
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

	public List<LabelStatisticsDto> createLabelStatistics(List<Transaction> transactions) {
		// TODO implementation
		return null;
	}

	public List<RecipientStatisticsDto> createYearlyRecipientStatistics(List<Transaction> transactions) {
		return createRecipientStatistics(transactions, transaction -> Year.from(transaction.getDate()));
	}

	public List<RecipientStatisticsDto> createMonthlyRecipientStatistics(List<Transaction> transactions) {
		return createRecipientStatistics(transactions, transaction -> YearMonth.from(transaction.getDate()));
	}

	// TODO clean up, separate to smaller methods
	public <P> List<RecipientStatisticsDto> createRecipientStatistics(List<Transaction> transactions, Function<Transaction, P> periodFunction) {
		List<RecipientStatisticsDto> response = new ArrayList<>();
		Set<P> allPeriods = new HashSet<>();
		transactions.forEach(transaction -> allPeriods.add(periodFunction.apply(transaction)));
		Map<Category, List<Transaction>> transactionsByCategories = transactions.stream().collect(Collectors.groupingBy(Transaction::getCategory));
		transactionsByCategories.forEach((category, transactionsByCategory) -> {
			RecipientStatisticsDto recipientStatisticsDto = new RecipientStatisticsDto();
			recipientStatisticsDto.setCategory(category.getName());
			recipientStatisticsDto.setTotalAmount(transactionsByCategory.stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
			Map<String, List<Transaction>> transactionsByRecipient = transactionsByCategory.stream().collect(Collectors.groupingBy(Transaction::getRecipient));
			transactionsByRecipient.forEach((recipient, transactionsByRecipientCategory) -> {
				RecipientStatisticDto recipientStatisticDto = new RecipientStatisticDto();
				recipientStatisticDto.setRecipient(recipient);
				Map<P, List<Transaction>> transactionsByPeriod = transactionsByRecipientCategory.stream().collect(Collectors.groupingBy(periodFunction));
				allPeriods.forEach(period -> {
					PeriodCategoryStatisticDto periodCategoryStatisticDto = new PeriodCategoryStatisticDto();
					periodCategoryStatisticDto.setPeriod(period.toString());
					periodCategoryStatisticDto.setAmount(transactionsByPeriod.getOrDefault(period, new ArrayList<>()).stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
					recipientStatisticDto.addPeriodCategoryStatisticDto(periodCategoryStatisticDto);
				});
				recipientStatisticDto.getPeriodCategoryStatistics().sort(Comparator.comparing(PeriodCategoryStatisticDto::getPeriod));
				PeriodCategoryStatisticDto periodCategoryStatisticDto = new PeriodCategoryStatisticDto();
				periodCategoryStatisticDto.setPeriod("Total");
				BigDecimal totalAmount = transactionsByRecipientCategory.stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add);
				periodCategoryStatisticDto.setAmount(totalAmount);
				recipientStatisticDto.addPeriodCategoryStatisticDto(periodCategoryStatisticDto);
				recipientStatisticDto.setTotalAmount(totalAmount);
				recipientStatisticsDto.addRecipientStatistics(recipientStatisticDto);
			});
			recipientStatisticsDto.getRecipientStatistics().sort(Comparator.comparing(RecipientStatisticDto::getTotalAmount));
			response.add(recipientStatisticsDto);
		});
		response.sort(Comparator.comparing(RecipientStatisticsDto::getTotalAmount));
		return response;
	}

	// TODO clean up, separate to smaller methods
	private <T, P> List<PeriodCategoryStatisticsDto> createCategoryStatistics(List<Transaction> transactions, Function<Transaction, T> groupingFunction, Function<T, String> nameFunction, Function<Transaction, P> periodFunction) {
		List<PeriodCategoryStatisticsDto> response = new ArrayList<>();
		Set<P> allPeriods = new HashSet<>();
		transactions.forEach(transaction -> allPeriods.add(periodFunction.apply(transaction)));
		Map<T, List<Transaction>> transactionsByCategories = transactions.stream().collect(Collectors.groupingBy(groupingFunction));
		transactionsByCategories.forEach((category, transactionsByCategory) -> {
			PeriodCategoryStatisticsDto periodCategoryStatisticsDto = new PeriodCategoryStatisticsDto();
			periodCategoryStatisticsDto.setCategory(nameFunction.apply(category));
			Map<P, List<Transaction>> transactionsByPeriod = transactionsByCategory.stream().collect(Collectors.groupingBy(periodFunction));
			allPeriods.forEach(period -> {
				PeriodCategoryStatisticDto periodCategoryStatisticDto = new PeriodCategoryStatisticDto();
				periodCategoryStatisticDto.setPeriod(period.toString());
				periodCategoryStatisticDto.setAmount(transactionsByPeriod.getOrDefault(period, new ArrayList<>()).stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
				periodCategoryStatisticsDto.addPeriodCategoryStatisticDto(periodCategoryStatisticDto);
			});
			periodCategoryStatisticsDto.getPeriodCategoryStatistics().sort(Comparator.comparing(PeriodCategoryStatisticDto::getPeriod));
			PeriodCategoryStatisticDto periodCategoryStatisticDto = new PeriodCategoryStatisticDto();
			periodCategoryStatisticDto.setPeriod("Total");
			BigDecimal totalAmount = transactionsByCategory.stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add);
			periodCategoryStatisticDto.setAmount(totalAmount);
			periodCategoryStatisticsDto.addPeriodCategoryStatisticDto(periodCategoryStatisticDto);
			periodCategoryStatisticsDto.setTotalAmount(totalAmount);
			response.add(periodCategoryStatisticsDto);
		});
		response.sort(Comparator.comparing(PeriodCategoryStatisticsDto::getTotalAmount));
		return response;
	}

}
