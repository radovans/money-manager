package cz.sinko.moneymanager.api;

import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApiError {

	@NonNull
	private List<String> errors;

}