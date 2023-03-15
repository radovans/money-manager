package cz.sinko.moneymanager.api.dto;

import static cz.sinko.moneymanager.api.dto.IncomeExpenseStatementDto.CZK;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

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
		DecimalFormat decimalFormat = getDecimalFormat();
		this.amountAbsFormatted = decimalFormat.format(amount.abs()) + CZK;
	}

	private static DecimalFormat getDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(customSymbol);
		return decimalFormat;
	}
}