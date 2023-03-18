package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cz.sinko.moneymanager.service.FormatUtil;
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

	public IncomeExpenseStatementDto(BigDecimal balance, BigDecimal income, BigDecimal expense) {
		this.balance = balance;
		this.income = income;
		this.expense = expense;
		BigDecimal savingsRatio = calculateSavingsRatio(income, expense);
		this.savingsRatio = savingsRatio.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		this.formattedBalance = FormatUtil.formatBigDecimal(balance);
		this.formattedIncome = FormatUtil.formatBigDecimal(income);
		this.formattedExpense = FormatUtil.formatBigDecimal(expense);
		this.formattedSavingsRatio = FormatUtil.formatPercentage(savingsRatio);
	}

	private BigDecimal calculateSavingsRatio(BigDecimal income, BigDecimal expense) {
		if (income.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		return income.add(expense).multiply(BigDecimal.valueOf(100)).divide(income, 2, RoundingMode.HALF_UP);
	}
}
