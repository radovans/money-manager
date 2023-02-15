package cz.sinko.moneymanager.repository.model;

import java.math.BigDecimal;
import java.time.MonthDay;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RecurrentTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Convert(
			converter = MonthDayDateAttributeConverter.class
	)
	private MonthDay firstPayment;
	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	private String recipient;

	private String note;

	private BigDecimal amount;

	private BigDecimal amountInCzk;

	private String currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_category_id", nullable = false)
	private MainCategory mainCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	private String label;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Enumerated(EnumType.STRING)
	private ExpenseType expenseType;

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		if (amount != null) {
			this.transactionType =
					amount.compareTo(BigDecimal.ZERO) > 0 ? TransactionType.INCOME : TransactionType.EXPENSE;
		}
	}

	public static class MonthDayDateAttributeConverter
			implements AttributeConverter<MonthDay, String> {

		@Override
		public String convertToDatabaseColumn(
				MonthDay attribute) {
			if (attribute != null) {
				return attribute.toString();
			}
			return null;
		}

		@Override
		public MonthDay convertToEntityAttribute(
				String dbData) {
			if (dbData != null) {
				return MonthDay.parse(dbData);
			}
			return null;
		}
	}
}
