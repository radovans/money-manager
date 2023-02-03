package cz.sinko.moneymanager.api.response;

import lombok.Data;

@Data
public class AccountTransactionDto extends TransactionDto{

	private AccountDto account;

}
