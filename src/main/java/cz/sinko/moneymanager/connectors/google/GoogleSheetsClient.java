package cz.sinko.moneymanager.connectors.google;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import cz.sinko.moneymanager.connectors.service.dto.ExchangeRateDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class GoogleSheetsClient {

	public static final String DATE_FORMAT = "d.M.yyyy";

	@Value("${google.sheets.key}")
	private String KEY;
	@Value("${google.sheets.spreadsheet.range}")
	private String RANGE;
	@Value("${google.sheets.spreadsheet.id}")
	private String SPREADSHEET_ID;

	private final GoogleSheetsApi googleSheetsApi;

	public GoogleSheetsClient() {
		WebClient webClient = WebClient.builder()
				.baseUrl("https://sheets.googleapis.com/v4/")
				.build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(webClient))
				.build();
		googleSheetsApi = factory.createClient(GoogleSheetsApi.class);
	}

	public List<ExchangeRateDto> getExchangeRates() {
		log.info("Getting exchange rates from Google Sheets API");
		Spreadsheet spreadsheet = googleSheetsApi.getSpreadsheetData(
				KEY,
				SPREADSHEET_ID,
				RANGE);
		List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();
		spreadsheet.getValues().stream().skip(1).forEach(row -> {
			ExchangeRateDto exchangeRateDto = createExchangeRateDto(row);
			exchangeRateDtos.add(exchangeRateDto);
		});
		log.info("Exchange rates from Google Sheets API: {}", exchangeRateDtos);
		return exchangeRateDtos;
	}

	@NonNull
	private static ExchangeRateDto createExchangeRateDto(List<String> row) {
		ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
		exchangeRateDto.setDate(LocalDate.parse(row.get(0), DateTimeFormatter.ofPattern(DATE_FORMAT)));
		exchangeRateDto.setEurExchangeRate(new BigDecimal(row.get(1).replace(",", ".")));
		return exchangeRateDto;
	}

}
