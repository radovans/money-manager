package cz.sinko.moneymanager.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Slf4j
public class UserController {

	@GetMapping("{id}")
	public ResponseEntity<AccountDto> getUser(@PathVariable String id) {
		return ResponseEntity.ok().body(new AccountDto());
	}

	@Data
	private class AccountDto {
		private String name = "Radovan";
		private String occupation = "Finance Minister";
		private String profileImageUrl = "https://media.licdn.com/dms/image/C4D03AQH4txEmGxo7nQ/profile-displayphoto-shrink_800_800/0/1610319630964?e=1683158400&v=beta&t=tTDZJeFOtvr95WADSBob89elA3T9ryKuPEcCaZmp23U";
	}

}
