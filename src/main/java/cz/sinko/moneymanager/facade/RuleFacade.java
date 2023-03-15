package cz.sinko.moneymanager.facade;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.RuleDto;
import cz.sinko.moneymanager.api.mapper.RuleMapper;
import cz.sinko.moneymanager.service.RuleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RuleFacade {

	private final RuleService ruleService;

	public List<RuleDto> getRules() {
		return RuleMapper.t().map(ruleService.find(Sort.by("id").ascending()));
	}

	public RuleDto createRule(RuleDto ruleDto) throws ResourceNotFoundException {
		return RuleMapper.t().map(ruleService.createRule(ruleDto));
	}

	public void deleteRule(Long id) {
		ruleService.deleteRule(id);
	}

	public RuleDto updateRule(Long id, RuleDto ruleDto) throws ResourceNotFoundException {
		return RuleMapper.t().map(ruleService.updateRule(id, ruleDto));
	}

}
