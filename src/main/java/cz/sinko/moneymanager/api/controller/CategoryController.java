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
import cz.sinko.moneymanager.api.mapper.SubcategoryMapper;
import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.api.response.SubcategoryDto;
import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.SubcategoryRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@Slf4j
public class CategoryController {

	private final CategoryRepository categoryRepository;

	@GetMapping
	public List<CategoryDto> getCategories() {
		log.info("Finding all categories.");
		return CategoryMapper.t().mapCategory(categoryRepository.findAll().stream().sorted(Comparator.comparing(Category::getName)).collect(Collectors.toList()));
	}

}
