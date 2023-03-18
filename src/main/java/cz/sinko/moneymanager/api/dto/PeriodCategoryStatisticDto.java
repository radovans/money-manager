package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;

import cz.sinko.moneymanager.service.FormatUtil;
import lombok.Data;

@Data
public class PeriodCategoryStatisticDto {

	private String period;
	private BigDecimal amount;
	private String amountFormatted;

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		this.amountFormatted = FormatUtil.formatBigDecimal(amount);
	}

}
