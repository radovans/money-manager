package cz.sinko.moneymanager.facade;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.api.ResourceNotFoundException;
import cz.sinko.moneymanager.api.dto.RuleDto;
import cz.sinko.moneymanager.api.mapper.RuleMapper;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.Rule;
import cz.sinko.moneymanager.repository.model.RuleType;
import cz.sinko.moneymanager.repository.model.Subcategory;
import cz.sinko.moneymanager.repository.model.Transaction;
import cz.sinko.moneymanager.service.CategoryService;
import cz.sinko.moneymanager.service.RuleService;
import cz.sinko.moneymanager.service.SubcategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RuleFacade {

	private final RuleService ruleService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SubcategoryService subcategoryService;

	public List<RuleDto> getRules() {
		return RuleMapper.t().map(ruleService.find(Sort.by("id").ascending()));
	}

	public RuleDto createRule(RuleDto ruleDto) throws ResourceNotFoundException {
		Subcategory subcategory = subcategoryService.find(ruleDto.getSubcategory());
		Category category = categoryService.find(ruleDto.getCategory());
		return RuleMapper.t().map(ruleService.createRule(ruleDto, category, subcategory));
	}

	public void deleteRule(Long id) {
		ruleService.deleteRule(id);
	}

	public RuleDto updateRule(Long id, RuleDto ruleDto) throws ResourceNotFoundException {
		Subcategory subcategory = subcategoryService.find(ruleDto.getSubcategory());
		Category category = categoryService.find(ruleDto.getCategory());
		return RuleMapper.t().map(ruleService.updateRule(id, ruleDto, category, subcategory));
	}

	public List<Transaction> applyRules(List<Transaction> transactions) {
		List<Rule> rules = ruleService.find();
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
				transaction.setCategory(categoryService.find(rule.getCategory().getName()));
			}
			if (rule.getSubcategory() != null) {
				transaction.setSubcategory(subcategoryService.find(rule.getSubcategory().getName()));
			}
			if (rule.getLabel() != null) {
				transaction.setLabel(rule.getLabel());
			}
		}
	}

}
