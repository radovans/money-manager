package cz.sinko.moneymanager.api.response;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountWithTransactionsDto extends AccountDto {

	private List<TransactionDto> transactions;

}
