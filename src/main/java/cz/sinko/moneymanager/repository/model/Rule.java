package cz.sinko.moneymanager.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
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
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subcategory_id")
	private Subcategory subcategory;

	private String label;

	public Rule(String recipient, String note, Category category, Subcategory subcategory) {
		this.recipient = recipient;
		this.note = note;
		this.category = category;
		this.subcategory = subcategory;
	}

	public Rule(String recipient, String note, Category category, Subcategory subcategory, String label) {
		this.recipient = recipient;
		this.note = note;
		this.category = category;
		this.subcategory = subcategory;
		this.label = label;
	}
}
