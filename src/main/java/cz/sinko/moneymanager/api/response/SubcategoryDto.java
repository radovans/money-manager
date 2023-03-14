package cz.sinko.moneymanager.api.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class SubcategoryDto {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long id;
	private String name;
	private String category;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private BigDecimal amount;

}
