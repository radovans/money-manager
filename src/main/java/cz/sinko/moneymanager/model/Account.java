package cz.sinko.moneymanager.model;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account {

	@Id
	private Integer id;
	private String name;

}
