package cz.sinko.moneymanager.facade;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.CategoryDto;
import cz.sinko.moneymanager.api.mapper.CategoryMapper;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CategoryFacade {

	private final CategoryService categoryService;

	public List<CategoryDto> getCategories() {
		return CategoryMapper.t().map(categoryService.find().stream().sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList()));
	}

	public CategoryDto createCategory(CategoryDto categoryDto) {
		return CategoryMapper.t().map(categoryService.createCategory(categoryDto));
	}

	public void deleteCategory(Long id) {
		categoryService.deleteCategory(id);
	}

	public CategoryDto updateCategory(Long id, CategoryDto categoryDto)
			throws ResourceNotFoundException {
		return CategoryMapper.t().map(categoryService.updateCategory(id, categoryDto));
	}

}
