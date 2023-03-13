package cz.sinko.moneymanager.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.mapper.RuleMapper;
import cz.sinko.moneymanager.api.response.RuleDto;
import cz.sinko.moneymanager.repository.RuleRepository;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Rule;
import cz.sinko.moneymanager.repository.model.RuleType;
import cz.sinko.moneymanager.repository.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RuleService {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubcategoryService subcategoryService;

	@Autowired
	private RuleRepository ruleRepository;

	public List<Rule> getRules(Sort sort) {
		return ruleRepository.findAll(sort);
	}

	public Rule createRule(RuleDto ruleDto) throws ResourceNotFoundException {
		Rule rule = RuleMapper.t().map(ruleDto);
		if (!ruleDto.isSkipTransaction()) {
			mapSubcategory(ruleDto, rule);
			mapCategory(ruleDto, rule);
		}
		return ruleRepository.save(rule);
	}

	private void mapSubcategory(RuleDto ruleDto, Rule rule) throws ResourceNotFoundException {
		if (ruleDto.getSubcategory() != null && !ruleDto.getSubcategory().isBlank()) {
			Subcategory subcategory = subcategoryService.findByName(ruleDto.getSubcategory());
			rule.setSubcategory(subcategory);
		}
	}

	private void mapCategory(RuleDto ruleDto, Rule rule) throws ResourceNotFoundException {
		if (ruleDto.getCategory() != null && !ruleDto.getCategory().isBlank()) {
			Category category = categoryService.findByName(ruleDto.getCategory());
			rule.setCategory(category);
		}
	}

	public void deleteRule(Long id) {
		ruleRepository.deleteById(id);
	}

	public Rule updateRule(Long id, RuleDto ruleDto) throws ResourceNotFoundException {
		Rule rule = ruleRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.createWith("Rule",
				" with id '" + ruleDto.getId() + "' was not found"));
		Rule newRule = RuleMapper.t().map(ruleDto);
		rule.setType(newRule.getType());
		rule.setKey(newRule.getKey());
		rule.setSkipTransaction(newRule.isSkipTransaction());
		rule.setRecipient(newRule.getRecipient());
		rule.setNote(newRule.getNote());
		if (!ruleDto.isSkipTransaction()) {
			mapSubcategory(ruleDto, rule);
			mapCategory(ruleDto, rule);
		} else {
			rule.setSubcategory(null);
			rule.setCategory(null);
		}
		rule.setLabel(newRule.getLabel());
		return ruleRepository.save(rule);
	}

	public List<Transaction> applyRules(List<Transaction> transactions) {
		List<Rule> rules = ruleRepository.findAll();
		Map<String, Rule> rulesBasedOnNote = rules.stream().filter(rule -> RuleType.NOTE.equals(rule.getType())).collect(Collectors.toMap(Rule::getKey, Function.identity()));
		Map<String, Rule> rulesBasedOnRecipient = rules.stream().filter(rule -> RuleType.RECIPIENT.equals(rule.getType())).collect(Collectors.toMap(Rule::getKey, Function.identity()));
		transactions.forEach(transaction -> {
			try {
				Optional<String> keyBasedOnNote = rulesBasedOnNote.keySet().parallelStream().filter(transaction.getNote()::contains).findFirst();
				applyRule(transaction, keyBasedOnNote, rulesBasedOnNote);
				Optional<String> keyBasedOnRecipient = rulesBasedOnRecipient.keySet().parallelStream().filter(transaction.getRecipient()::contains).findFirst();
				applyRule(transaction, keyBasedOnRecipient, rulesBasedOnRecipient);
			} catch (ResourceNotFoundException e) {
				throw new RuntimeException("Category or main category not found", e);
			}
		});
		return transactions;
	}

	private void applyRule(Transaction transaction, Optional<String> key, Map<String, Rule> rules)
			throws ResourceNotFoundException {
		if (key.isPresent()) {
			Rule rule = rules.get(key.get());
			if (rule.getRecipient() != null) {
				transaction.setRecipient(rule.getRecipient());
			}
			if (rule.getNote() != null) {
				transaction.setNote(rule.getNote());
			}
			if (rule.getCategory() != null) {
				transaction.setCategory(categoryService.findByName(rule.getCategory().getName()));
			}
			if (rule.getSubcategory() != null) {
				transaction.setSubcategory(subcategoryService.findByName(rule.getSubcategory().getName()));
			}
			if (rule.getLabel() != null) {
				transaction.setLabel(rule.getLabel());
			}
		}
	}
}