package cz.sinko.moneymanager.connectors.google;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import cz.sinko.moneymanager.connectors.dto.ExchangeRateDto;
import cz.sinko.moneymanager.connectors.service.ExchangeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class GoogleSheetClient implements ExchangeService {

	private final GoogleSheetService googleSheetService;

	public GoogleSheetClient() {
		WebClient webClient = WebClient.builder()
				.baseUrl("https://sheets.googleapis.com/v4/")
				.build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory
				.builder(WebClientAdapter.forClient(webClient))
				.build();
		googleSheetService = factory.createClient(GoogleSheetService.class);
	}

	@Override
	public List<ExchangeRateDto> getExchangeRates() {
		Spreadsheet spreadsheet = googleSheetService.getSpreadsheetData(
				"",
				"Sheet1!A1:C1181",
				"");
		List<ExchangeRateDto> exchangeRateDtos = new ArrayList<>();
		spreadsheet.getValues().stream().skip(1).forEach(row -> {
			ExchangeRateDto exchangeRateDto = new ExchangeRateDto();
			exchangeRateDto.setDate(LocalDate.parse(row.get(0), DateTimeFormatter.ofPattern("d.M.yyyy")));
			exchangeRateDto.setEurExchangeRate(new BigDecimal(row.get(1).replace(",", ".")));
			exchangeRateDtos.add(exchangeRateDto);
		});
		return exchangeRateDtos;
	}

	@Override
	public ExchangeRateDto getExchangeRate(LocalDate date) {
		return null;
	}

	@Override
	public Double convertEurToCzk(Double eur) {
		return null;
	}

}
