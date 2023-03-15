package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

import lombok.Data;

@Data
public class MonthStatisticDto {

	private YearMonth month;
	private BigDecimal balance;
	private BigDecimal incomes;
	private BigDecimal expenses;
	private BigDecimal cumulativeBalance;

}