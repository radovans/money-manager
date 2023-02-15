package cz.sinko.moneymanager.api.response;

import static cz.sinko.moneymanager.api.response.IncomeExpenseStatementDto.CZK;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MainCategoryStatisticsDto extends MainCategoryDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal amount;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal amountAbs;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String amountAbsFormatted;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal percentage;
	@JsonInclude(JsonInclude.Include.NON_NULL)
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