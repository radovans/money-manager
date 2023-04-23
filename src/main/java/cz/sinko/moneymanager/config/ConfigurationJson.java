package cz.sinko.moneymanager.config;

import cz.sinko.moneymanager.api.dto.PlannedTransactionDto;
import cz.sinko.moneymanager.api.dto.RecurrentTransactionDto;
import cz.sinko.moneymanager.api.dto.RuleDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ConfigurationJson {

	private List<String> accounts;
	private List<String> categories;
	private Map<String, String> subcategories;
	private List<RuleDto> rules;
	private List<PlannedTransactionDto> plannedTransactions;
	private List<RecurrentTransactionDto> recurrentTransactions;

}
