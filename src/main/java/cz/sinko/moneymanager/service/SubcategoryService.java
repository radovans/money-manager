package cz.sinko.moneymanager.service;

import java.util.List;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.SubcategoryDto;
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

	public List<Subcategory> findAll() {
		return subcategoryRepository.findAll();
	}

	public Subcategory find(String subcategory) throws ResourceNotFoundException {
		return subcategoryRepository.findByName(subcategory).orElseThrow(() -> ResourceNotFoundException.createWith("Subcategory",
				" with name '" + subcategory + "' was not found"));
	}

	public Subcategory find(Long subcategoryId) throws ResourceNotFoundException {
		return subcategoryRepository.findById(subcategoryId).orElseThrow(() -> ResourceNotFoundException.createWith("Subcategory",
				" with id '" + subcategoryId + "' was not found"));
	}

	public List<Subcategory> findByCategory(Category category) {
		return subcategoryRepository.findByCategory(category);
	}

	public Subcategory createSubcategory(Subcategory subcategory, Category category) {
		subcategory.setCategory(category);
		return subcategoryRepository.save(subcategory);
	}

	public void deleteSubcategory(Long id) {
		subcategoryRepository.deleteById(id);
	}

	public Subcategory updateSubcategory(Long id, SubcategoryDto subcategoryDto, Category newCategory)
			throws ResourceNotFoundException {
		Subcategory subcategory = find(id);
		subcategory.setName(subcategoryDto.getName());
		if (subcategory.getCategory() != null && subcategoryDto.getCategory() != null
				&& !subcategory.getCategory().getName().equals(subcategoryDto.getCategory())) {
			subcategory.setCategory(newCategory);
		}
		return subcategoryRepository.save(subcategory);
	}

}
