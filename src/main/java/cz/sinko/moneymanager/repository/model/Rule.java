package cz.sinko.moneymanager.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Enumerated(EnumType.STRING)
	private RuleType type;

	@NotNull
	private String key;

	@NotNull
	@Column(name = "skip_transaction", columnDefinition = "boolean default false")
	private boolean skipTransaction;

	private String recipient;

	private String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_category_id")
	private MainCategory mainCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	private String label;

	public Rule(String recipient, String note, MainCategory mainCategory, Category category) {
		this.recipient = recipient;
		this.note = note;
		this.mainCategory = mainCategory;
		this.category = category;
	}

	public Rule(String recipient, String note, MainCategory mainCategory, Category category, String label) {
		this.recipient = recipient;
		this.note = note;
		this.mainCategory = mainCategory;
		this.category = category;
		this.label = label;
	}
}
