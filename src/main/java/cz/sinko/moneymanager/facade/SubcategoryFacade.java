package cz.sinko.moneymanager.facade;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.SubcategoryDto;
import cz.sinko.moneymanager.api.mapper.SubcategoryMapper;
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

	public List<SubcategoryDto> getSubcategories(String category)
			throws ResourceNotFoundException {
		if (category != null && !category.isBlank()) {
			Category categoryEntity = categoryService.find(category);
			return SubcategoryMapper.t().map(subcategoryService.findByCategory(categoryEntity).stream().sorted(Comparator.comparing(Subcategory::getName)).collect(Collectors.toList()));
		} else {
			return SubcategoryMapper.t().map(subcategoryService.findAll().stream().sorted(Comparator.comparing(Subcategory::getName)).collect(Collectors.toList()));
		}
	}

	public SubcategoryDto createSubcategory(SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		Subcategory subcategory = SubcategoryMapper.t().map(subcategoryDto);
		Category category = categoryService.find(subcategoryDto.getCategory());
		return SubcategoryMapper.t().map(subcategoryService.createSubcategory(subcategory, category));
	}

	public void deleteSubcategory(Long id) {
		subcategoryService.deleteSubcategory(id);
	}

	public SubcategoryDto updateSubcategory(Long id, SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		Category newCategory = categoryService.find(subcategoryDto.getCategory());
		Subcategory oldSubcategory = subcategoryService.find(id);
		transactionService.updateTransactions(newCategory, oldSubcategory);
		return SubcategoryMapper.t().map(subcategoryService.updateSubcategory(id, subcategoryDto, newCategory));
	}

}
