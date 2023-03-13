package cz.sinko.moneymanager.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.response.IncomeExpenseStatementDto;
import cz.sinko.moneymanager.api.response.CategoryStatisticsDto;
import cz.sinko.moneymanager.api.response.SpendingCategoriesDto;
import cz.sinko.moneymanager.repository.TransactionRepository;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StatisticsService {

	private final TransactionRepository transactionRepository;

	public void calculateIncomeExpense(List<Account> accounts) {
		for (Account account : accounts) {
			calculateIncomeExpense(account);
		}
	}

	public void calculateIncomeExpense(Account account) {
		List<Transaction> transactions = transactionRepository.findByAccountId(account.getId());
		account.setBalance(transactions.stream().map(Transaction::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
		account.setIncome(transactions.stream().map(Transaction::getAmountInCzk).filter(amount ->
				amount.compareTo(BigDecimal.ZERO) > 0).reduce(BigDecimal.ZERO, BigDecimal::add));
		account.setExpense(transactions.stream().map(Transaction::getAmountInCzk).filter(amount ->
				amount.compareTo(BigDecimal.ZERO) < 0).reduce(BigDecimal.ZERO, BigDecimal::add));
		account.setNumberOfTransactions(transactions.size());
	}

	public IncomeExpenseStatementDto createIncomeExpenseStatement(List<Transaction> transactions) {
		BigDecimal balance = calculateBalance(transactions);
		BigDecimal income = calculateIncome(transactions);
		BigDecimal expense = calculateExpense(transactions);
		return new IncomeExpenseStatementDto(balance, income, expense);
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

	private static void calculatePercentage(List<CategoryStatisticsDto> categories) {
		BigDecimal totalAmount = categories.stream().map(CategoryStatisticsDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add).abs();
		categories.forEach(category -> category.setPercentage(category.getAmount().abs().multiply(BigDecimal.valueOf(100)).divide(totalAmount, RoundingMode.HALF_UP)));
	}

}
