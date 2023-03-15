package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import lombok.Data;

@Data
public class IncomeExpenseStatementDto {

	private BigDecimal balance;
	private BigDecimal income;
	private BigDecimal expense;
	private BigDecimal savingsRatio;
	private String formattedBalance;
	private String formattedIncome;
	private String formattedExpense;
	private String formattedSavingsRatio;

	public static final String CZK = " CZK";

	public IncomeExpenseStatementDto(BigDecimal balance, BigDecimal income, BigDecimal expense) {
		this.balance = balance;
		this.income = income;
		this.expense = expense;
		BigDecimal savingsRatio = calculateSavingsRatio(income, expense);
		this.savingsRatio = savingsRatio.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

		DecimalFormat decimalFormat = getDecimalFormat();
		this.formattedBalance = decimalFormat.format(balance) + CZK;
		this.formattedIncome = decimalFormat.format(income) + CZK;
		this.formattedExpense = decimalFormat.format(expense) + CZK;
		this.formattedSavingsRatio = decimalFormat.format(savingsRatio) + "%";
	}

	private static DecimalFormat getDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");
		DecimalFormatSymbols customSymbol = new DecimalFormatSymbols();
		customSymbol.setGroupingSeparator(' ');
		decimalFormat.setDecimalFormatSymbols(customSymbol);
		return decimalFormat;
	}

	private BigDecimal calculateSavingsRatio(BigDecimal income, BigDecimal expense) {
		if (income.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		return income.add(expense).multiply(BigDecimal.valueOf(100)).divide(income, 2, RoundingMode.HALF_UP);
	}
}
