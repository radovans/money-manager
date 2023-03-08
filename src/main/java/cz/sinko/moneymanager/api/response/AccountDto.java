package cz.sinko.moneymanager.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class AccountDto {

	private Long id;
	private String name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private IncomeExpenseStatementDto incomeExpenseStatement;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer numberOfTransactions;

}
