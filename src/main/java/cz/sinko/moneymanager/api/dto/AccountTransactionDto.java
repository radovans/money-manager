package cz.sinko.moneymanager.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountTransactionDto extends TransactionDto {

	private String account;

}
