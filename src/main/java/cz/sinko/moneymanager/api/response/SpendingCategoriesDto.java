package cz.sinko.moneymanager.api.response;

import static cz.sinko.moneymanager.api.response.IncomeExpenseStatementDto.CZK;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SpendingCategoriesDto {

	private List<MainCategoryStatisticsDto> categories = new ArrayList<>();
	private BigDecimal total;
	private String totalFormatted;

	public void setTotal(BigDecimal total) {
		this.total = total;
		DecimalFormat decimalFormat = getDecimalFormat();
		this.totalFormatted = decimalFormat.format(total) + CZK;
	}

	private static DecimalFormat getDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(customSymbol);
		return decimalFormat;
	}

}
