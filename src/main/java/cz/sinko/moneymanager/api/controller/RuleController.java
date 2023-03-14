package cz.sinko.moneymanager.api.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.RuleMapper;
import cz.sinko.moneymanager.api.response.RuleDto;
import cz.sinko.moneymanager.service.RuleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rules")
@AllArgsConstructor
@Slf4j
public class RuleController {

	private final RuleService ruleService;

	@GetMapping
	public List<RuleDto> getRules() {
		log.info("Finding all rules.");
		return RuleMapper.t().map(ruleService.find(Sort.by("id").ascending()));
	}

	@PostMapping
	public RuleDto createRule(@RequestBody RuleDto ruleDto) throws ResourceNotFoundException {
		log.info("Creating new rule: '{}'.", ruleDto);
		return RuleMapper.t().map(ruleService.createRule(ruleDto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRule(@PathVariable Long id) {
		log.info("Deleting rule with id: '{}'.", id);
		ruleService.deleteRule(id);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}")
	public RuleDto updateRule(@PathVariable Long id, @RequestBody RuleDto ruleDto) throws ResourceNotFoundException {
		log.info("Updating rule with id: '{}'.", id);
		return RuleMapper.t().map(ruleService.updateRule(id, ruleDto));
	}

}
