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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.SubcategoryMapper;
import cz.sinko.moneymanager.api.response.SubcategoryDto;
import cz.sinko.moneymanager.facade.SubcategoryFacade;
import cz.sinko.moneymanager.repository.SubcategoryRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.service.CategoryService;
import cz.sinko.moneymanager.service.SubcategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/subcategories")
@AllArgsConstructor
@Slf4j
public class SubcategoryController {
	private final CategoryService categoryService;

	private final SubcategoryRepository subcategoryRepository;

	private final SubcategoryService subcategoryService;

	private final SubcategoryFacade subcategoryFacade;

	@GetMapping
	public List<SubcategoryDto> getSubcategories(@RequestParam(required = false) String category)
			throws ResourceNotFoundException {
		log.info("Finding all subcategories.");
		if (category != null && !category.isBlank()) {
			Category categoryEntity = categoryService.find(category);
			return SubcategoryMapper.t().mapSubcategory(subcategoryRepository.findByCategory(categoryEntity).stream().sorted(Comparator.comparing(Subcategory::getName)).collect(Collectors.toList()));
		} else {
			return SubcategoryMapper.t().mapSubcategory(subcategoryRepository.findAll().stream().sorted(Comparator.comparing(Subcategory::getName)).collect(Collectors.toList()));
		}
	}

	@PostMapping
	public SubcategoryDto createSubcategory(@RequestBody SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		log.info("Creating new Subcategory: '{}'.", subcategoryDto);
		return SubcategoryMapper.t().mapSubcategory(subcategoryService.createSubcategory(subcategoryDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteSubcategory(@PathVariable Long id) {
		log.info("Deleting Subcategory with id: '{}'.", id);
		subcategoryService.deleteSubcategory(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public SubcategoryDto updateSubcategory(@PathVariable Long id, @RequestBody SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		log.info("Updating Subcategory with id: '{}'.", id);
		return SubcategoryMapper.t().mapSubcategory(subcategoryFacade.updateSubcategory(id, subcategoryDto));
	}

}
