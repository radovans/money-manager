package cz.sinko.moneymanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.RuleDto;
import cz.sinko.moneymanager.api.mapper.RuleMapper;
import cz.sinko.moneymanager.repository.RuleRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Rule;
import cz.sinko.moneymanager.repository.model.Subcategory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RuleService {
	@Autowired
	private RuleRepository ruleRepository;

	public List<Rule> find() {
		return ruleRepository.findAll();
	}

	public List<Rule> find(Sort sort) {
		return ruleRepository.findAll(sort);
	}

	public Rule find(Long ruleId) throws ResourceNotFoundException {
		return ruleRepository.findById(ruleId).orElseThrow(() -> ResourceNotFoundException.createWith("Rule",
				" with id '" + ruleId + "' was not found"));
	}

	public Rule createRule(RuleDto ruleDto, Category category, Subcategory subcategory) {
		Rule rule = RuleMapper.t().map(ruleDto);
		if (!ruleDto.isSkipTransaction()) {
			if (category != null) {
				rule.setCategory(category);
			}
			if (subcategory != null) {
				rule.setSubcategory(subcategory);
			}
		}
		return ruleRepository.save(rule);
	}

	public void deleteRule(Long id) {
		ruleRepository.deleteById(id);
	}

	public Rule updateRule(Long id, RuleDto ruleDto, Category category, Subcategory subcategory)
			throws ResourceNotFoundException {
		Rule rule = find(id);
		rule.setType(ruleDto.getType());
		rule.setKey(ruleDto.getKey());
		rule.setSkipTransaction(ruleDto.isSkipTransaction());
		rule.setRecipient(ruleDto.getRecipient());
		rule.setNote(ruleDto.getNote());
		if (!ruleDto.isSkipTransaction()) {
			if (category != null) {
				rule.setCategory(category);
			}
			if (subcategory != null) {
				rule.setSubcategory(subcategory);
			}
		} else {
			rule.setSubcategory(null);
			rule.setCategory(null);
		}
		rule.setLabel(ruleDto.getLabel());
		return ruleRepository.save(rule);
	}

}