package cz.sinko.moneymanager.api.dto;

import static cz.sinko.moneymanager.api.dto.IncomeExpenseStatementDto.CZK;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransactionsDto {

	private List<AccountTransactionDto> transactions;
	private long total;
	private BigDecimal totalAmount;
	private String totalAmountFormatted;
	private int numberOfElements;
	private long totalElements;

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
		DecimalFormat decimalFormat = getDecimalFormat();
		this.totalAmountFormatted = decimalFormat.format(totalAmount) + CZK;
	}

	private static DecimalFormat getDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(customSymbol);
		return decimalFormat;
	}

}
