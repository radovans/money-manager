package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cz.sinko.moneymanager.service.FormatUtil;
import lombok.Data;

@Data
public class RecipientStatisticsDto {

	private String category;
	private BigDecimal totalAmount;
	private String formattedTotalAmount;
	private List<RecipientStatisticDto> recipientStatistics = new ArrayList<>();

	public void addRecipientStatistics(RecipientStatisticDto recipientStatisticDto) {
		recipientStatistics.add(recipientStatisticDto);
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
		this.formattedTotalAmount = FormatUtil.formatBigDecimal(totalAmount);
	}

}
