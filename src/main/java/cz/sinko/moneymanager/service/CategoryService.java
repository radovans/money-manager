package cz.sinko.moneymanager.service;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.CategoryMapper;
import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.model.Category;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public Category find(Long categoryId) throws ResourceNotFoundException {
		return categoryRepository.findById(categoryId).orElseThrow(() -> ResourceNotFoundException.createWith("Category",
				" with id '" + categoryId + "' was not found"));
	}

	public Category find(String category) throws ResourceNotFoundException {
		return categoryRepository.findByName(category).orElseThrow(() -> ResourceNotFoundException.createWith("Category",
				" with name '" + category + "' was not found"));
	}

	public List<Category> find() {
		return categoryRepository.findAll();
	}

	public Category createCategory(CategoryDto categoryDto) {
		Category category = CategoryMapper.t().map(categoryDto);
		return categoryRepository.save(category);
	}

	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}

	public Category updateCategory(Long id, CategoryDto categoryDto) throws ResourceNotFoundException {
		Category category = find(id);
		category.setName(categoryDto.getName());
		return categoryRepository.save(category);
	}

}
