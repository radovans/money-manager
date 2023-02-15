package cz.sinko.moneymanager.api.response;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class RecurrentStatisticDto {

	private YearMonth month;
	private BigDecimal balance;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal cumulativeBalance;
	private BigDecimal expenses;
	private BigDecimal cumulativeExpenses;
	private BigDecimal incomes;
	private BigDecimal cumulativeIncomes;

}