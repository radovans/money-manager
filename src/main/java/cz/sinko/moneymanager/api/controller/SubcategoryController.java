package cz.sinko.moneymanager.api.controller;

import java.util.List;

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
import cz.sinko.moneymanager.api.dto.SubcategoryDto;
import cz.sinko.moneymanager.facade.SubcategoryFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/subcategories")
@AllArgsConstructor
@Slf4j
public class SubcategoryController {

	private final SubcategoryFacade subcategoryFacade;

	@GetMapping
	public List<SubcategoryDto> getSubcategories(@RequestParam(required = false) String category)
			throws ResourceNotFoundException {
		log.info("Finding all subcategories.");
		return subcategoryFacade.getSubcategories(category);
	}

	@PostMapping
	public SubcategoryDto createSubcategory(@RequestBody SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		log.info("Creating new Subcategory: '{}'.", subcategoryDto);
		return subcategoryFacade.createSubcategory(subcategoryDto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteSubcategory(@PathVariable Long id) {
		log.info("Deleting Subcategory with id: '{}'.", id);
		subcategoryFacade.deleteSubcategory(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public SubcategoryDto updateSubcategory(@PathVariable Long id, @RequestBody SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		log.info("Updating Subcategory with id: '{}'.", id);
		return subcategoryFacade.updateSubcategory(id, subcategoryDto);
	}

}
