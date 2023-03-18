package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import cz.sinko.moneymanager.service.FormatUtil;
import lombok.Data;

@Data
public class TransactionDto {

	private Long id;
	private LocalDate date;
	private String recipient;
	private String note;
	private BigDecimal amount;
	private BigDecimal amountInCzk;
	private String formattedAmountInCzk;
	private String currency;
	private String subcategory;
	private String category;
	private String account;
	private String label;

	public void setAmountInCzk(BigDecimal amountInCzk) {
		this.amountInCzk = amountInCzk;
		this.formattedAmountInCzk = FormatUtil.formatBigDecimal(amountInCzk);
	}

}
