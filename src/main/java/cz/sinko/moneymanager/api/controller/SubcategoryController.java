package cz.sinko.moneymanager.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.SubcategoryMapper;
import cz.sinko.moneymanager.api.response.SubcategoryDto;
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

	@GetMapping
	public List<SubcategoryDto> getSubcategories(@RequestParam(required = false) String category)
			throws ResourceNotFoundException {
		log.info("Finding all subcategories.");
		if (category != null && !category.isBlank()) {
			Category categoryEntity = categoryService.findByName(category);
			return SubcategoryMapper.t().mapSubcategory(subcategoryRepository.findByCategory(categoryEntity).stream().sorted(Comparator.comparing(Subcategory::getName)).collect(Collectors.toList()));
		} else {
			return SubcategoryMapper.t().mapSubcategory(subcategoryRepository.findAll().stream().sorted(Comparator.comparing(Subcategory::getName)).collect(Collectors.toList()));
		}
	}

}
