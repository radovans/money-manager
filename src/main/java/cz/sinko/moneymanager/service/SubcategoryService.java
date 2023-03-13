package cz.sinko.moneymanager.service;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.repository.SubcategoryRepository;
import cz.sinko.moneymanager.repository.model.Subcategory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class SubcategoryService {
	private final SubcategoryRepository subcategoryRepository;

	public Subcategory findByName(String subcategory) throws ResourceNotFoundException {
		return subcategoryRepository.findByName(subcategory).orElseThrow(() -> ResourceNotFoundException.createWith("Subcategory",
				" with name '" + subcategory + "' was not found"));
	}

	public void save(Subcategory subcategory) {
		subcategoryRepository.save(subcategory);
	}
}
