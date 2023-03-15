package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

import lombok.Data;

@Data
public class RecurrentStatisticDto {

	private YearMonth month;
	private BigDecimal balance;
	private BigDecimal cumulativeBalance;
	private BigDecimal expenses;
	private BigDecimal cumulativeExpenses;
	private BigDecimal incomes;
	private BigDecimal cumulativeIncomes;

}