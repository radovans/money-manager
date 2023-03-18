package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.util.List;

import cz.sinko.moneymanager.service.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountTransactionsDto {

	private List<AccountTransactionDto> transactions;
	private long total;
	private BigDecimal totalAmount;
	private String totalAmountFormatted;
	private int numberOfElements;
	private long totalElements;

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
		this.totalAmountFormatted = FormatUtil.formatBigDecimal(totalAmount);
	}

}
