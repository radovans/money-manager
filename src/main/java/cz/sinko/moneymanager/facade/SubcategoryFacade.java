package cz.sinko.moneymanager.facade;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.response.SubcategoryDto;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.service.CategoryService;
import cz.sinko.moneymanager.service.SubcategoryService;
import cz.sinko.moneymanager.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SubcategoryFacade {

	private final SubcategoryService subcategoryService;

	private final CategoryService categoryService;

	private final TransactionService transactionService;

	public Subcategory updateSubcategory(Long id, SubcategoryDto subcategoryDto) throws ResourceNotFoundException {
		Category newCategory = categoryService.find(subcategoryDto.getCategory());
		Subcategory oldSubcategory = subcategoryService.find(id);
		transactionService.updateTransactions(newCategory, oldSubcategory);
		return subcategoryService.updateSubcategory(id, subcategoryDto);
	}

}
