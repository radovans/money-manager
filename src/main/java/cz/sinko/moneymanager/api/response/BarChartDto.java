package cz.sinko.moneymanager.api.response;

import java.util.List;

import lombok.Data;

@Data
public class BarChartDto {

	private List<CategoryDto> categories;

}
