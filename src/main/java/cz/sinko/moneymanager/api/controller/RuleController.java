package cz.sinko.moneymanager.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.sinko.moneymanager.api.mapper.RuleMapper;
import cz.sinko.moneymanager.api.response.RuleDto;
import cz.sinko.moneymanager.repository.RuleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rules")
@AllArgsConstructor
@Slf4j
public class RuleController {

	private final RuleRepository ruleRepository;

	@GetMapping
	public List<RuleDto> getRules() {
		log.info("Finding all rules.");
		return RuleMapper.t().map(ruleRepository.findAll());
	}
}
