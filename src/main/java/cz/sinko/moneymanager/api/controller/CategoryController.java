package cz.sinko.moneymanager.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.CategoryDto;
import cz.sinko.moneymanager.facade.CategoryFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
public class CategoryController {

	private final CategoryFacade categoryFacade;

	@GetMapping
	public List<CategoryDto> getCategories() {
		log.info("Finding all categories.");
		return categoryFacade.getCategories();
	}

	@PostMapping
	public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
		log.info("Creating new Category: '{}'.", categoryDto);
		return categoryFacade.createCategory(categoryDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
		log.info("Deleting Category with id: '{}'.", id);
		categoryFacade.deleteCategory(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public CategoryDto updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto)
			throws ResourceNotFoundException {
		log.info("Updating Category with id: '{}'.", id);
		return categoryFacade.updateCategory(id, categoryDto);
	}

}
