package cz.sinko.moneymanager.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.sinko.moneymanager.repository.CategoryRepository;
import cz.sinko.moneymanager.repository.MainCategoryRepository;
import cz.sinko.moneymanager.repository.RuleRepository;
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
	private MainCategoryRepository mainCategoryRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private RuleRepository ruleRepository;

	public List<Transaction> applyRules(List<Transaction> transactions) {
		List<Rule> rules = ruleRepository.findAll();
		Map<String, Rule> rulesBasedOnNote = rules.stream().filter(rule -> RuleType.NOTE.equals(rule.getType())).collect(Collectors.toMap(Rule::getKey, Function.identity()));
		Map<String, Rule> rulesBasedOnRecipient = rules.stream().filter(rule -> RuleType.RECIPIENT.equals(rule.getType())).collect(Collectors.toMap(Rule::getKey, Function.identity()));
		transactions.forEach(transaction -> {
			Optional<String> keyBasedOnNote = rulesBasedOnNote.keySet().parallelStream().filter(transaction.getNote()::contains).findFirst();
			applyRule(transaction, keyBasedOnNote, rulesBasedOnNote);
			Optional<String> keyBasedOnRecipient = rulesBasedOnRecipient.keySet().parallelStream().filter(transaction.getRecipient()::contains).findFirst();
			applyRule(transaction, keyBasedOnRecipient, rulesBasedOnRecipient);
		});
		return transactions;
	}

	private void applyRule(Transaction transaction, Optional<String> key, Map<String, Rule> rules) {
		if (key.isPresent()) {
			Rule rule = rules.get(key.get());
			if (rule.getRecipient() != null) {
				transaction.setRecipient(rule.getRecipient());
			}
			if (rule.getNote() != null) {
				transaction.setNote(rule.getNote());
			}
			if (rule.getMainCategory() != null) {
				transaction.setMainCategory(mainCategoryRepository.findByName(rule.getMainCategory().getName()));
			}
			if (rule.getCategory() != null) {
				transaction.setCategory(categoryRepository.findByName(rule.getCategory().getName()));
			}
			if (rule.getLabel() != null) {
				transaction.setLabel(rule.getLabel());
			}
		}
	}

}