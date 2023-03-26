package cz.sinko.moneymanager.connectors.service;

import java.time.LocalDate;
import java.util.List;

import cz.sinko.moneymanager.connectors.dto.ExchangeRateDto;

public interface ExchangeService {

	List<ExchangeRateDto> getExchangeRates();

	ExchangeRateDto getExchangeRate(LocalDate date);

	Double convertEurToCzk(Double eur);

}
