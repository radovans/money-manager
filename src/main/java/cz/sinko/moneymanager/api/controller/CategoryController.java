package cz.sinko.moneymanager.api.controller;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.CategoryDto;
import cz.sinko.moneymanager.facade.CategoryFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
public class CategoryController {

	private final CategoryFacade categoryFacade;

	@GetMapping
	public ResponseEntity<List<CategoryDto>> getCategories() {
		log.info("Finding all categories.");
		return ResponseEntity.ok().body(categoryFacade.getCategories());
	}

	@PostMapping
	public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
		log.info("Creating new Category: '{}'.", categoryDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryFacade.createCategory(categoryDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		log.info("Deleting Category with id: '{}'.", id);
		categoryFacade.deleteCategory(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto)
			throws ResourceNotFoundException {
		log.info("Updating Category with id: '{}'.", id);
		return ResponseEntity.ok().body(categoryFacade.updateCategory(id, categoryDto));
	}

}
