package cz.sinko.moneymanager.service;

import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.repository.MainCategoryRepository;
import cz.sinko.moneymanager.repository.model.MainCategory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class MainCategoryService {

	private final MainCategoryRepository mainCategoryRepository;

	public MainCategory findByName(String mainCategory) throws ResourceNotFoundException {
		return mainCategoryRepository.findByName(mainCategory).orElseThrow(() -> ResourceNotFoundException.createWith("MainCategory",
				" with name '" + mainCategory + "' was not found"));
	}

	public void save(MainCategory mainCategory) {
		mainCategoryRepository.save(mainCategory);
	}
}
