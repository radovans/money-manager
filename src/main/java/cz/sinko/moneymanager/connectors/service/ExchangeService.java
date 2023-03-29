package cz.sinko.moneymanager.connectors.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import cz.sinko.moneymanager.connectors.service.dto.ExchangeRateDto;

public interface ExchangeService {

	List<ExchangeRateDto> getExchangeRates();

	ExchangeRateDto getExchangeRate(LocalDate date);

	BigDecimal convertCurrencyToCzk(Currency currency, LocalDate date, BigDecimal amount);

}
