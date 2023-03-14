package cz.sinko.moneymanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.SubcategoryMapper;
import cz.sinko.moneymanager.api.response.SubcategoryDto;
import cz.sinko.moneymanager.repository.SubcategoryRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SubcategoryService {
	private final SubcategoryRepository subcategoryRepository;

	private final CategoryService categoryService;

	private final TransactionService transactionService;

	public Subcategory find(String subcategory) throws ResourceNotFoundException {
		return subcategoryRepository.findByName(subcategory).orElseThrow(() -> ResourceNotFoundException.createWith("Subcategory",
				" with name '" + subcategory + "' was not found"));
	}

	public Subcategory find(Long subcategoryId) throws ResourceNotFoundException {
		Optional<Subcategory> subcategory = subcategoryRepository.findById(subcategoryId);
		if (subcategory.isEmpty()) {
			throw ResourceNotFoundException.createWith("Subcategory", " with id '" + subcategoryId + "' was not found");
		}
		return subcategory.get();
	}

	public Subcategory createSubcategory(SubcategoryDto subcategoryDto) throws ResourceNotFoundException {
		Subcategory subcategory = SubcategoryMapper.t().map(subcategoryDto);
		Category category = categoryService.find(subcategoryDto.getCategory());
		subcategory.setCategory(category);
		return subcategoryRepository.save(subcategory);
	}

	public void deleteSubcategory(Long id) {
		subcategoryRepository.deleteById(id);
	}

	public Subcategory updateSubcategory(Long id, SubcategoryDto subcategoryDto) throws ResourceNotFoundException {
		Subcategory subcategory = subcategoryRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.createWith("Subcategory",
				" with id '" + subcategoryDto.getId() + "' was not found"));
		subcategory.setName(subcategoryDto.getName());
		if (subcategory.getCategory() != null && subcategoryDto.getCategory() != null
				&& !subcategory.getCategory().getName().equals(subcategoryDto.getCategory())) {
			Category category = categoryService.find(subcategoryDto.getCategory());
			updateTransactions(category, subcategory);
			subcategory.setCategory(category);
		}
		return subcategoryRepository.save(subcategory);
	}

	private void updateTransactions(Category newCategory, Subcategory oldSubcategory) {
		List<Transaction> transactions = transactionService.find(oldSubcategory);
		for (Transaction transaction : transactions) {
			if (transaction.getCategory() != null && newCategory != null
					&& !transaction.getCategory().getName().equals(newCategory.getName())) {
				transaction.setCategory(newCategory);
				transactionService.update(transaction);
			}
		}
	}

}
