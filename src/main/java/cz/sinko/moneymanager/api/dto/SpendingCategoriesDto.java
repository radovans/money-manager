package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cz.sinko.moneymanager.service.FormatUtil;
import lombok.Data;

@Data
public class SpendingCategoriesDto {

	private List<CategoryStatisticsDto> categories = new ArrayList<>();
	private BigDecimal total;
	private String totalFormatted;

	public void setTotal(BigDecimal total) {
		this.total = total;
		this.totalFormatted = FormatUtil.formatBigDecimal(total);
	}

}
