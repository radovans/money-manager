package cz.sinko.moneymanager.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsDto {

	private IncomeExpenseStatementDto totalIncomeExpenseStatement;
	private IncomeExpenseStatementDto lastYearIncomeExpenseStatement;
	private IncomeExpenseStatementDto yearToDateIncomeExpenseStatement;

}
