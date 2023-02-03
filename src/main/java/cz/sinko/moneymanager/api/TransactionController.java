package cz.sinko.moneymanager.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.mapper.TransactionMapper;
import cz.sinko.moneymanager.api.response.AccountTransactionDto;
import cz.sinko.moneymanager.model.Transaction;
import cz.sinko.moneymanager.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
@Slf4j
public class TransactionController {

	private final TransactionRepository transactionRepository;

	@GetMapping
	public List<AccountTransactionDto> getTransactions() {
		log.info("Finding all transactions.");
		List<Transaction> transactions = transactionRepository.findAll();
		return TransactionMapper.t().map(transactions);
	}
}
