package cz.sinko.moneymanager.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TransactionDto {

	private LocalDate date;
	private String recipient;
	private String note;
	private BigDecimal amount;
	private BigDecimal amountInCzk;
	private String currency;
	private MainCategoryDto mainCategory;
	private CategoryDto category;
	private String label;

}
