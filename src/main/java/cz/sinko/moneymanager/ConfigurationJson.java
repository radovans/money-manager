package cz.sinko.moneymanager;

import java.util.List;
import java.util.Map;

import cz.sinko.moneymanager.api.response.PlannedTransactionDto;
import cz.sinko.moneymanager.api.response.RecurrentTransactionDto;
import cz.sinko.moneymanager.api.response.RuleDto;
import lombok.Data;

@Data
public class ConfigurationJson {

	private List<String> accounts;
	private List<String> categories;
	private Map<String, String> subcategories;
	private List<RuleDto> rules;
	private List<PlannedTransactionDto> plannedTransactions;
	private List<RecurrentTransactionDto> recurrentTransactions;

}
