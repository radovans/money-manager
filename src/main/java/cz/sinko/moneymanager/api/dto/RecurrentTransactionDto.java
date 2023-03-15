package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RecurrentTransactionDto {

	private String firstPayment;
	private String frequency;
	private String recipient;
	private String note;
	private BigDecimal amount;
	private String currency;
	private String subcategory;
	private String category;
	private String account;
	private String label;
	private String transactionType;
	private String expenseType;

}
