package cz.sinko.moneymanager.repository.model;

import java.math.BigDecimal;
import java.time.MonthDay;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@Convert(converter = MonthDayDateAttributeConverter.class)
	private MonthDay firstPayment;

	@Enumerated(EnumType.STRING)
	private Frequency frequency;

	private String recipient;

	private String note;

	private BigDecimal amount;

	private BigDecimal amountInCzk;

	private String currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subcategory_id", nullable = false)
	private Subcategory subcategory;

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
