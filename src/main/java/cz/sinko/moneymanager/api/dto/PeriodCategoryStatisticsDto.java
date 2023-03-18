package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PeriodCategoryStatisticsDto {

	private String category;
	private BigDecimal totalAmount;
	private List<PeriodCategoryStatisticDto> periodCategoryStatistics = new ArrayList<>();

	public void addPeriodCategoryStatisticDto(PeriodCategoryStatisticDto periodCategoryStatisticDto) {
		periodCategoryStatistics.add(periodCategoryStatisticDto);
	}

}
