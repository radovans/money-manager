package cz.sinko.moneymanager.api.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SubcategoryDto {

	private Long id;
	private String name;
	private String category;
	private BigDecimal amount;

}
