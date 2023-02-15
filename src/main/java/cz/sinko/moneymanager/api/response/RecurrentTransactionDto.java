package cz.sinko.moneymanager.api.response;

import java.math.BigDecimal;
import java.time.MonthDay;
import java.time.Period;

import lombok.Data;

@Data
public class RecurrentTransactionDto {

	private String firstPayment;
	private String frequency;
	private String recipient;
	private String note;
	private BigDecimal amount;
	private String currency;
	private String mainCategory;
	private String category;
	private String account;
	private String label;
	private String transactionType;
	private String expenseType;

}
