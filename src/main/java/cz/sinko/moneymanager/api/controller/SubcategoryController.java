package cz.sinko.moneymanager.api.controller;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.SubcategoryDto;
import cz.sinko.moneymanager.facade.SubcategoryFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subcategories")
@AllArgsConstructor
@Slf4j
public class SubcategoryController {

	private final SubcategoryFacade subcategoryFacade;

	@GetMapping
	public ResponseEntity<List<SubcategoryDto>> getSubcategories(@RequestParam(required = false) String category)
			throws ResourceNotFoundException {
		log.info("Finding all subcategories.");
		return ResponseEntity.ok().body(subcategoryFacade.getSubcategories(category));
	}

	@PostMapping
	public ResponseEntity<SubcategoryDto> createSubcategory(@RequestBody SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		log.info("Creating new Subcategory: '{}'.", subcategoryDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(subcategoryFacade.createSubcategory(subcategoryDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSubcategory(@PathVariable Long id) {
		log.info("Deleting Subcategory with id: '{}'.", id);
		subcategoryFacade.deleteSubcategory(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<SubcategoryDto> updateSubcategory(@PathVariable Long id, @RequestBody SubcategoryDto subcategoryDto)
			throws ResourceNotFoundException {
		log.info("Updating Subcategory with id: '{}'.", id);
		return ResponseEntity.ok().body(subcategoryFacade.updateSubcategory(id, subcategoryDto));
	}

}
