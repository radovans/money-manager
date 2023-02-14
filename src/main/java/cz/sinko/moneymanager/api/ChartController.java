package cz.sinko.moneymanager.api;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.response.BarChartDto;
import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.model.Category;
import cz.sinko.moneymanager.model.MainCategory;
import cz.sinko.moneymanager.model.Transaction;
import cz.sinko.moneymanager.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/charts")
@AllArgsConstructor
@Slf4j
public class ChartController {

	private final TransactionRepository transactionRepository;

	@GetMapping("/barChart")
	public BarChartDto getAccountWithTransactions() {
		List<Transaction> transactions = transactionRepository.findAll();
		Map<MainCategory, List<Transaction>> transactionsByMainCategories = transactions.stream().collect(Collectors.groupingBy(Transaction::getMainCategory));

		return mapBarChartDto(transactionsByMainCategories);
	}

	private static BarChartDto mapBarChartDto(Map<MainCategory, List<Transaction>> transactionsByCategories) {
		BarChartDto response = new BarChartDto();
		List<CategoryDto> categoryDtos = transactionsByCategories.entrySet().stream().map(entry -> {
			CategoryDto categoryDto = new CategoryDto();
			categoryDto.setName(entry.getKey().getName());
			categoryDto.setAmount(entry.getValue().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
			return categoryDto;
		}).collect(Collectors.toList());
		response.setCategories(categoryDtos.stream().sorted(Comparator.comparing(CategoryDto::getAmount)).collect(Collectors.toList()));
		return response;
	}
}
