package cz.sinko.moneymanager.connectors.google;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/spreadsheets")
public interface GoogleSheetService {

	@GetExchange("/{spreadsheetId}/values/{range}?key={key}")
	Spreadsheet getSpreadsheetData(@PathVariable("spreadsheetId") String spreadsheetId,
			@PathVariable("range") String range, @PathVariable("key") String key);
}
