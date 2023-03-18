package cz.sinko.moneymanager.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.dto.PeriodCategoryStatisticsDto;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.StatisticsService;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StatisticsFacade {

	private final TransactionService transactionService;

	private final StatisticsService statisticsService;

	public List<PeriodCategoryStatisticsDto> getYearlyCategoryStatistics() {
		List<Transaction> transactions = transactionService.find();
		return statisticsService.createYearlyCategoryStatistics(transactions);
	}

	public List<PeriodCategoryStatisticsDto> getMonthlyCategoryStatistics() {
		List<Transaction> transactions = transactionService.find();
		return statisticsService.createMonthlyCategoryStatistics(transactions);
	}

	public List<PeriodCategoryStatisticsDto> getYearlySubcategoryStatistics() {
		List<Transaction> transactions = transactionService.find();
		return statisticsService.createYearlySubcategoryStatistics(transactions);
	}

	public List<PeriodCategoryStatisticsDto> getMonthlySubcategoryStatistics() {
		List<Transaction> transactions = transactionService.find();
		return statisticsService.createMonthlySubcategoryStatistics(transactions);
	}
}
