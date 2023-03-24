package cz.sinko.moneymanager.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

	private Long id;
	private String name;

}
