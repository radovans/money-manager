package cz.sinko.moneymanager.api.dto;

import cz.sinko.moneymanager.repository.model.RuleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class RuleDto {

	private Long id;
	private RuleType type;
	private String key;
	private boolean skipTransaction;
	private String recipient;
	private String note;
	private String subcategory;
	private String category;
	private String label;

}
