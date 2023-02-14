package cz.sinko.moneymanager.service;

import lombok.Data;

@Data
public class Rule {

	private String recipient;
	private String note;
	private String mainCategory;
	private String category;
	private String label;

	public Rule(String recipient, String note, String mainCategory, String category) {
		this.recipient = recipient;
		this.note = note;
		this.mainCategory = mainCategory;
		this.category = category;
	}

	public Rule(String recipient, String note, String mainCategory, String category, String label) {
		this.recipient = recipient;
		this.note = note;
		this.mainCategory = mainCategory;
		this.category = category;
		this.label = label;
	}
}
