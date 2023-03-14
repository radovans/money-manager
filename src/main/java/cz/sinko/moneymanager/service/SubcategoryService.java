package cz.sinko.moneymanager.service;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.SubcategoryMapper;
import cz.sinko.moneymanager.api.response.SubcategoryDto;
import cz.sinko.moneymanager.repository.SubcategoryRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SubcategoryService {
	private final SubcategoryRepository subcategoryRepository;

	private final CategoryService categoryService;

	public Subcategory find(String subcategory) throws ResourceNotFoundException {
		return subcategoryRepository.findByName(subcategory).orElseThrow(() -> ResourceNotFoundException.createWith("Subcategory",
				" with name '" + subcategory + "' was not found"));
	}

	public Subcategory find(Long subcategoryId) throws ResourceNotFoundException {
		return subcategoryRepository.findById(subcategoryId).orElseThrow(() -> ResourceNotFoundException.createWith("Subcategory",
				" with id '" + subcategoryId + "' was not found"));
	}

	public Subcategory createSubcategory(SubcategoryDto subcategoryDto) throws ResourceNotFoundException {
		Subcategory subcategory = SubcategoryMapper.t().map(subcategoryDto);
		Category category = categoryService.find(subcategoryDto.getCategory());
		subcategory.setCategory(category);
		return subcategoryRepository.save(subcategory);
	}

	public void deleteSubcategory(Long id) {
		subcategoryRepository.deleteById(id);
	}

	public Subcategory updateSubcategory(Long id, SubcategoryDto subcategoryDto) throws ResourceNotFoundException {
		Subcategory subcategory = find(id);
		subcategory.setName(subcategoryDto.getName());
		if (subcategory.getCategory() != null && subcategoryDto.getCategory() != null
				&& !subcategory.getCategory().getName().equals(subcategoryDto.getCategory())) {
			Category category = categoryService.find(subcategoryDto.getCategory());
			subcategory.setCategory(category);
		}
		return subcategoryRepository.save(subcategory);
	}

}
