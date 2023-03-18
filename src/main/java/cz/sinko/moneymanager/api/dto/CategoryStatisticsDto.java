package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;

import cz.sinko.moneymanager.service.FormatUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryStatisticsDto extends CategoryDto {

	private BigDecimal amount;
	private BigDecimal amountAbs;
	private String amountAbsFormatted;
	private BigDecimal percentage;
	private Boolean isSubcategory;

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		this.amountAbs = amount.abs();
		this.amountAbsFormatted = FormatUtil.formatBigDecimal(amount.abs());
	}

}