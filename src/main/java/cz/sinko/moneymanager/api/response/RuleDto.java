package cz.sinko.moneymanager.api.response;

import cz.sinko.moneymanager.repository.model.RuleType;
import lombok.Data;

@Data
public class RuleDto {

	private String key;
	private RuleType type;
	private String recipient;
	private String note;
	private String mainCategory;
	private String category;
	private String label;

}
