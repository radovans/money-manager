package cz.sinko.moneymanager.api.response;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class MonthStatisticDto {

	private YearMonth month;
	private BigDecimal balance;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal incomes;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal expenses;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal cumulativeAmount;

}