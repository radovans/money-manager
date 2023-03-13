package cz.sinko.moneymanager.service;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.model.Category;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public Category findByName(String category) throws ResourceNotFoundException {
		return categoryRepository.findByName(category).orElseThrow(() -> ResourceNotFoundException.createWith("Category",
				" with name '" + category + "' was not found"));
	}

	public void save(Category category) {
		categoryRepository.save(category);
	}
}
