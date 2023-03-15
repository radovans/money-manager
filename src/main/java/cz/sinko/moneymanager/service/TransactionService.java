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
import cz.sinko.moneymanager.api.dto.TransactionDto;
import cz.sinko.moneymanager.api.mapper.TransactionMapper;
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
	private final SubcategoryService subcategoryService;
	private final AccountService accountService;

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

	public Transaction find(Long transactionId) throws ResourceNotFoundException {
		return transactionRepository.findById(transactionId).orElseThrow(() -> ResourceNotFoundException.createWith("Transaction",
				" with id '" + transactionId + "' was not found"));
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

	public Transaction createTransaction(TransactionDto transactionDto) throws ResourceNotFoundException {
		Transaction transaction = TransactionMapper.t().map(transactionDto);
		if (transactionDto.getSubcategory() != null) {
			transaction.setSubcategory(subcategoryService.find(transactionDto.getSubcategory()));
		}
		if (transactionDto.getCategory() != null) {
			transaction.setCategory(categoryService.find(transactionDto.getCategory()));
		}
		if (transactionDto.getAccount() != null) {
			transaction.setAccount(accountService.find(transactionDto.getAccount()));
		}
		return transactionRepository.save(transaction);
	}

	public void deleteTransaction(Long id) {
		transactionRepository.deleteById(id);
	}

	public Transaction updateTransaction(Long id, TransactionDto transactionDto) throws ResourceNotFoundException {
		Transaction transaction = find(id);
		transaction.setDate(transactionDto.getDate());
		transaction.setRecipient(transactionDto.getRecipient());
		transaction.setNote(transactionDto.getNote());
		transaction.setAmount(transactionDto.getAmount());
		transaction.setAmountInCzk(transactionDto.getAmountInCzk());
		transaction.setCurrency(transactionDto.getCurrency());
		transaction.setCategory(categoryService.find(transactionDto.getCategory()));
		transaction.setSubcategory(subcategoryService.find(transactionDto.getSubcategory()));
		transaction.setAccount(accountService.find(transactionDto.getAccount()));
		transaction.setLabel(transactionDto.getLabel());
		return transactionRepository.save(transaction);
	}

	public void updateTransactions(Category newCategory, Subcategory oldSubcategory) {
		List<Transaction> transactions = find(oldSubcategory);
		for (Transaction transaction : transactions) {
			if (transaction.getCategory() != null && newCategory != null
					&& !transaction.getCategory().getName().equals(newCategory.getName())) {
				transaction.setCategory(newCategory);
				update(transaction);
			}
		}
	}

}
