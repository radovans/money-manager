package cz.sinko.moneymanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.CategoryMapper;
import cz.sinko.moneymanager.api.mapper.CategoryMapperImpl;
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

	public Category find(String category) throws ResourceNotFoundException {
		return categoryRepository.findByName(category).orElseThrow(() -> ResourceNotFoundException.createWith("Category",
				" with name '" + category + "' was not found"));
	}

	public List<Category> find() {
		return categoryRepository.findAll();
	}

	public Category find(Long categoryId) throws ResourceNotFoundException {
		Optional<Category> category = categoryRepository.findById(categoryId);
		if (category.isEmpty()) {
			throw ResourceNotFoundException.createWith("Category", " with id '" + categoryId + "' was not found");
		}
		return category.get();
	}

	public Category createCategory(CategoryDto categoryDto) throws ResourceNotFoundException {
		Category category = new CategoryMapperImpl().map(categoryDto);
		return categoryRepository.save(category);
	}

	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}

	public Category updateCategory(Long id, CategoryDto categoryDto) throws ResourceNotFoundException {
		Category category = categoryRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.createWith("Category",
				" with id '" + categoryDto.getId() + "' was not found"));
		category.setName(categoryDto.getName());
		return categoryRepository.save(category);
	}

}
