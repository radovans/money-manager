package cz.sinko.moneymanager.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.repository.TransactionRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final CategoryService categoryService;

	public Page<Transaction> find(Sort sort, int page, int size, String search, LocalDate from, LocalDate to, String category)
			throws ResourceNotFoundException {
		if (search != null) {
			if (category != null) {
				Category categoryEntity = categoryService.find(category);
				return transactionRepository.findByNoteLikeAndDateBetweenAndCategory(PageRequest.of(page, size, sort),
						"%" + search + "%", from, to, categoryEntity);
			}
			return transactionRepository.findByNoteLikeAndDateBetween(PageRequest.of(page, size, sort),
					"%" + search + "%", from, to);
		} else {
			if (category != null) {
				Category categoryEntity = categoryService.find(category);
				return transactionRepository.findByDateBetweenAndCategory(PageRequest.of(page, size, sort), from, to, categoryEntity);
			}
			return transactionRepository.findByDateBetween(PageRequest.of(page, size, sort), from, to);
		}
	}

	public List<Transaction> find(LocalDate from, LocalDate to) {
		return transactionRepository.findByDateBetween(from, to);
	}

	public List<Transaction> find(LocalDate from, LocalDate to, String category)
			throws ResourceNotFoundException {
		Category categoryEntity = categoryService.find(category);
		return transactionRepository.findByDateBetweenAndCategory(from, to, categoryEntity);
	}

	public List<Transaction> find(YearMonth from, YearMonth to, String category)
			throws ResourceNotFoundException {
		Category categoryEntity = categoryService.find(category);
		return transactionRepository.findByDateBetweenAndCategory(from.atDay(1), to.atEndOfMonth(), categoryEntity);
	}

	public List<Transaction> find(YearMonth from, YearMonth to) {
		return find(from.atDay(1), to.atEndOfMonth());
	}

	public List<Transaction> find() {
		return transactionRepository.findAll();
	}

	public List<Transaction> find(Long accountId) {
		return transactionRepository.findByAccountId(accountId);
	}

	public List<Transaction> find(Subcategory subcategory) {
		return transactionRepository.findBySubcategory(subcategory);
	}

	public List<Transaction> filterTransactionsByYear(List<Transaction> transactions, int year) {
		return transactions.stream().filter(transaction ->
				transaction.getDate().getYear() == year).collect(Collectors.toList());
	}

	public void update(Transaction transaction) {
		transactionRepository.save(transaction);
	}
}
