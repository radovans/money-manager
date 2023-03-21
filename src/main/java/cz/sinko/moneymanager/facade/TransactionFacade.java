package cz.sinko.moneymanager.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.AccountTransactionDto;
import cz.sinko.moneymanager.api.dto.AccountTransactionsDto;
import cz.sinko.moneymanager.api.dto.SortTransactionFields;
import cz.sinko.moneymanager.api.dto.TransactionDto;
import cz.sinko.moneymanager.api.mapper.TransactionMapper;
import cz.sinko.moneymanager.repository.model.Account;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.AccountService;
import cz.sinko.moneymanager.service.CategoryService;
import cz.sinko.moneymanager.service.SubcategoryService;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TransactionFacade {

	private final TransactionService transactionService;

	private final CategoryService categoryService;

	private final SubcategoryService subcategoryService;

	private final AccountService accountService;

	public AccountTransactionsDto getTransactions(String sort, int page, int size, String search, LocalDateTime from, LocalDateTime to, String category)
			throws ResourceNotFoundException {
		String parsedSort = parseSort(sort);
		String parsedDirection = parseDirection(sort);
		Page<Transaction> transactions = getTransactions(page, size, search, from, to, category, parsedSort, parsedDirection);
		List<AccountTransactionDto> accountTransactionDtos = TransactionMapper.t().mapToAccountTransactionDtoList(transactions);
		AccountTransactionsDto response = createResponse(transactions, accountTransactionDtos);
		response.setTotalElements(transactions.getTotalElements());
		response.setNumberOfElements(transactions.getNumberOfElements());
		return response;
	}

	public TransactionDto createTransaction(TransactionDto transactionDto)
			throws ResourceNotFoundException {
		Category category = categoryService.find(transactionDto.getCategory());
		Subcategory subcategory = subcategoryService.find(transactionDto.getSubcategory());
		Account account = accountService.find(transactionDto.getAccount());
		return TransactionMapper.t().map(transactionService.createTransaction(transactionDto, category, subcategory, account));
	}

	public ResponseEntity<?> deleteTransaction(Long id) {
		transactionService.deleteTransaction(id);
		return ResponseEntity.ok().build();
	}

	public TransactionDto updateTransaction(Long id, TransactionDto transactionDto)
			throws ResourceNotFoundException {
		Category category = categoryService.find(transactionDto.getCategory());
		Subcategory subcategory = subcategoryService.find(transactionDto.getSubcategory());
		Account account = accountService.find(transactionDto.getAccount());
		return TransactionMapper.t().map(transactionService.updateTransaction(id, transactionDto, category, subcategory, account));
	}

	private static AccountTransactionsDto createResponse(Page<Transaction> transactions, List<AccountTransactionDto> accountTransactionDtos) {
		AccountTransactionsDto response = new AccountTransactionsDto();
		response.setTransactions(accountTransactionDtos);
		response.setTotalAmount(transactions.getTotalElements() > 0 ?
				transactions.getContent().stream().map(Transaction::getAmountInCzk).reduce(BigDecimal::add).get() :
				BigDecimal.ZERO);
		return response;
	}

	private Page<Transaction> getTransactions(int page, int size, String search, LocalDateTime from, LocalDateTime to, String category, String parsedSort, String parsedDirection)
			throws ResourceNotFoundException {
		Page<Transaction> transactions;
		if (size <= 0) {
			transactions = transactionService.find(Sort.by(new Sort.Order(Sort.Direction.fromString(parsedDirection), parsedSort)), page, Integer.MAX_VALUE, search, LocalDate.from(from), LocalDate.from(to), category);
		} else {
			transactions = transactionService.find(Sort.by(new Sort.Order(Sort.Direction.fromString(parsedDirection), parsedSort)), page, size, search, LocalDate.from(from), LocalDate.from(to), category);
		}
		return transactions;
	}

	private String parseDirection(String sort) {
		if (sort != null) {
			return sort.split(",")[1].split(":")[1].replace("\"", "").replace("}", "");
		} else {
			return Sort.Direction.ASC.name();
		}
	}

	private String parseSort(String sort) {
		if (sort != null) {
			String parsedSort = sort.split(",")[0].split(":")[1].replace("\"", "");
			if (parsedSort.equals(SortTransactionFields.formattedAmountInCzk.name())) {
				return SortTransactionFields.amount.name();
			}
			return parsedSort;
		} else {
			return SortTransactionFields.id.name();
		}
	}
}
