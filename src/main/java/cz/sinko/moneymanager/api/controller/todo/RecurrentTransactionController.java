package cz.sinko.moneymanager.api.controller.todo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.dto.RecurrentStatisticDto;
import cz.sinko.moneymanager.api.dto.RecurrentTransactionDto;
import cz.sinko.moneymanager.api.mapper.RecurrentTransactionMapper;
import cz.sinko.moneymanager.repository.RecurrentTransactionRepository;
import cz.sinko.moneymanager.repository.model.Frequency;
import cz.sinko.moneymanager.repository.model.RecurrentTransaction;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.repository.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// TODO clean up
@RestController
@RequestMapping("/recurrent-transactions")
@AllArgsConstructor
@Slf4j
public class RecurrentTransactionController {

	// TODO create and call facade
	private final RecurrentTransactionRepository recurrentTransactionRepository;

	@GetMapping
	public List<RecurrentTransactionDto> getRecurrentTransactions() {
		log.info("Finding all recurrent transactions.");
		return RecurrentTransactionMapper.t().map(recurrentTransactionRepository.findAll());
	}

	@GetMapping("/statistics")
	public List<RecurrentStatisticDto> getRecurrentStatistics() {
		log.info("Calculating recurrent transactions statistics.");
		List<RecurrentTransaction> yearlyRecurrentTransactions = recurrentTransactionRepository.findByFrequency(Frequency.YEARLY);
		List<Transaction> transactions = new ArrayList<>();
		yearlyRecurrentTransactions.forEach(recurrentTransaction -> {
			Transaction transaction = RecurrentTransactionMapper.t().mapToTransaction(recurrentTransaction);
			transactions.add(transaction);
		});

		List<RecurrentTransaction> monthlyRecurrentTransactions = recurrentTransactionRepository.findByFrequency(Frequency.MONTHLY);
		monthlyRecurrentTransactions.forEach(recurrentTransaction -> {
			for (Month month : Month.values()) {
				Transaction transaction = RecurrentTransactionMapper.t().mapToTransaction(recurrentTransaction);
				transaction.setDate(transaction.getDate().withMonth(month.getValue()));
				transactions.add(transaction);
			}
		});

		Map<YearMonth, List<Transaction>> transactionsByMonth = transactions.stream()
				.collect(Collectors.groupingBy(transaction -> YearMonth.from(transaction.getDate()),
						TreeMap::new,
						Collectors.toList()));

		Map<YearMonth, Double> expensesByMonth = new TreeMap<>();
		Map<YearMonth, Double> incomesByMonth = new TreeMap<>();

		transactionsByMonth.forEach((month, transactionsInMonth) -> {
			expensesByMonth.put(month, transactionsInMonth.stream()
					.filter(transaction -> transaction.getTransactionType() == TransactionType.EXPENSE)
					.mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue())
					.sum());
			incomesByMonth.put(month, transactionsInMonth.stream()
					.filter(transaction -> transaction.getTransactionType() == TransactionType.INCOME)
					.mapToDouble(transaction -> transaction.getAmountInCzk().doubleValue())
					.sum());
		});

		List<RecurrentStatisticDto> response = new ArrayList<>();
		transactionsByMonth.forEach((month, transactionsInMonth) -> {
			RecurrentStatisticDto recurrentStatisticDto = new RecurrentStatisticDto();
			recurrentStatisticDto.setMonth(month);
			BigDecimal incomes = BigDecimal.valueOf(incomesByMonth.get(month)).setScale(2, RoundingMode.HALF_UP);
			BigDecimal expenses = BigDecimal.valueOf(expensesByMonth.get(month)).setScale(2, RoundingMode.HALF_UP);
			recurrentStatisticDto.setBalance(incomes.add(expenses));
			recurrentStatisticDto.setIncomes(incomes);
			recurrentStatisticDto.setExpenses(expenses);
			response.add(recurrentStatisticDto);
		});
		response.forEach(monthStatisticDto -> monthStatisticDto.setCumulativeBalance(response.stream()
				.filter(monthStatistic -> monthStatistic.getMonth().isBefore(monthStatisticDto.getMonth()))
				.map(RecurrentStatisticDto::getBalance)
				.reduce(BigDecimal.ZERO, BigDecimal::add).add(monthStatisticDto.getBalance())));
		response.forEach(monthStatisticDto -> monthStatisticDto.setCumulativeIncomes(response.stream()
				.filter(monthStatistic -> monthStatistic.getMonth().isBefore(monthStatisticDto.getMonth()))
				.map(RecurrentStatisticDto::getIncomes)
				.reduce(BigDecimal.ZERO, BigDecimal::add).add(monthStatisticDto.getIncomes())));
		response.forEach(monthStatisticDto -> monthStatisticDto.setCumulativeExpenses(response.stream()
				.filter(monthStatistic -> monthStatistic.getMonth().isBefore(monthStatisticDto.getMonth()))
				.map(RecurrentStatisticDto::getExpenses)
				.reduce(BigDecimal.ZERO, BigDecimal::add).add(monthStatisticDto.getExpenses())));
		return response;
	}

}
