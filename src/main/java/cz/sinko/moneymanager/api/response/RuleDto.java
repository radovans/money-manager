package cz.sinko.moneymanager.api.response;

import cz.sinko.moneymanager.repository.model.RuleType;
import lombok.Data;

@Data
public class RuleDto {

	private Long id;
	private RuleType type;
	private String key;
	private boolean skipTransaction;
	private String recipient;
	private String note;
	private String mainCategory;
	private String category;
	private String label;

}
