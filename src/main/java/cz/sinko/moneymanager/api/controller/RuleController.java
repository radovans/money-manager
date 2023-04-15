package cz.sinko.moneymanager.api.controller;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.RuleDto;
import cz.sinko.moneymanager.facade.RuleFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rules")
@AllArgsConstructor
@Slf4j
public class RuleController {

	private final RuleFacade ruleFacade;

	@GetMapping
	public ResponseEntity<List<RuleDto>> getRules() {
		log.info("Finding all rules.");
		return ResponseEntity.ok().body(ruleFacade.getRules());
	}

	@PostMapping
	public ResponseEntity<RuleDto> createRule(@RequestBody RuleDto ruleDto) throws ResourceNotFoundException {
		log.info("Creating new rule: '{}'.", ruleDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(ruleFacade.createRule(ruleDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
		log.info("Deleting rule with id: '{}'.", id);
		ruleFacade.deleteRule(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<RuleDto> updateRule(@PathVariable Long id, @RequestBody RuleDto ruleDto) throws ResourceNotFoundException {
		log.info("Updating rule with id: '{}'.", id);
		return ResponseEntity.ok().body(ruleFacade.updateRule(id, ruleDto));
	}

}
