package cz.sinko.moneymanager.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiError {

	@NonNull
	private List<String> errors;

	private String stackTrace;

}