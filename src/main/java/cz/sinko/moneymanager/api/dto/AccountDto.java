package cz.sinko.moneymanager.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AccountDto {

	private Long id;

	@NotBlank(message = "Name may not be blank")
	private String name;

}
