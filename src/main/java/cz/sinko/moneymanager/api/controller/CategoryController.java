package cz.sinko.moneymanager.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
import cz.sinko.moneymanager.api.mapper.CategoryMapper;
import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public List<CategoryDto> getCategories() {
		log.info("Finding all categories.");
		return CategoryMapper.t().mapCategory(categoryService.find().stream().sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList()));
	}

	@PostMapping
	public CategoryDto createCategory(@RequestBody CategoryDto CategoryDto) throws ResourceNotFoundException {
		log.info("Creating new Category: '{}'.", CategoryDto);
		return CategoryMapper.t().mapCategory(categoryService.createCategory(CategoryDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
		log.info("Deleting Category with id: '{}'.", id);
		categoryService.deleteCategory(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public CategoryDto updateCategory(@PathVariable Long id, @RequestBody CategoryDto CategoryDto)
			throws ResourceNotFoundException {
		log.info("Updating Category with id: '{}'.", id);
		return CategoryMapper.t().mapCategory(categoryService.updateCategory(id, CategoryDto));
	}

}
