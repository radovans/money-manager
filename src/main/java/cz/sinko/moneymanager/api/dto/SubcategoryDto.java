package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class SubcategoryDto {

	private Long id;
	private String name;
	private String category;
	private BigDecimal amount;

}
