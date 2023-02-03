package cz.sinko.moneymanager.api.response;

import java.util.List;

import lombok.Data;

@Data
public class AccountWithTransactionsDto extends AccountDto {

	private List<TransactionDto> transactions;

}
