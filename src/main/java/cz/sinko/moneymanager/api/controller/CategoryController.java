package cz.sinko.moneymanager.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.CategoryMapper;
import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.api.response.MainCategoryDto;
import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.MainCategoryRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.MainCategory;
import cz.sinko.moneymanager.service.MainCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
public class CategoryController {

	private final CategoryRepository categoryRepository;

	private final MainCategoryRepository mainCategoryRepository;

	private final MainCategoryService mainCategoryService;

	@GetMapping("/main")
	public List<MainCategoryDto> getMainCategories() {
		log.info("Finding all main categories.");
		return CategoryMapper.t().mapMainCategory(mainCategoryRepository.findAll().stream().sorted(Comparator.comparing(MainCategory::getName)).collect(Collectors.toList()));
	}

	@GetMapping
	public List<CategoryDto> getCategories(@RequestParam(required = false) String mainCategory)
			throws ResourceNotFoundException {
		log.info("Finding all categories.");
		if (mainCategory != null && !mainCategory.isBlank()) {
			MainCategory mainCategoryEntity = mainCategoryService.findByName(mainCategory);
			return CategoryMapper.t().mapCategory(categoryRepository.findByMainCategory(mainCategoryEntity).stream().sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList()));
		} else {
			return CategoryMapper.t().mapCategory(categoryRepository.findAll().stream().sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList()));
		}
	}

}
