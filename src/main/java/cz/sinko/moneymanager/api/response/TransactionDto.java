package cz.sinko.moneymanager.api.response;

import static cz.sinko.moneymanager.api.response.IncomeExpenseStatementDto.CZK;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;

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
	private String label;

	public void setAmountInCzk(BigDecimal amountInCzk) {
		this.amountInCzk = amountInCzk;
		this.formattedAmountInCzk = getDecimalFormat().format(amountInCzk) + CZK;
	}

	private static DecimalFormat getDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(customSymbol);
		return decimalFormat;
	}

}
