package cz.sinko.moneymanager.connectors.google;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.sinko.moneymanager.connectors.service.CacheService;
import cz.sinko.moneymanager.connectors.service.ExchangeService;
import cz.sinko.moneymanager.connectors.service.dto.ExchangeRateDto;
import cz.sinko.moneymanager.connectors.service.entity.CacheData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class GoogleSheetsServiceImpl implements ExchangeService {

	public static final String REDIS_KEY_EXCHANGE_RATES_PREFIX = "exchangeRates-";
	public static final String ALL = "all";
	private final GoogleSheetsClient googleSheetsClient;

	private final CacheService cacheService;

	private final ObjectMapper objectMapper;

	@Override
	public List<ExchangeRateDto> getExchangeRates() {
		Optional<CacheData> optionalCacheData = cacheService.getKey(REDIS_KEY_EXCHANGE_RATES_PREFIX + ALL);
		if (optionalCacheData.isPresent()) {
			String exchangeRatesAsString = optionalCacheData.get().getValue();
			return Arrays.asList(Objects.requireNonNull(jsonStringToObject(exchangeRatesAsString, ExchangeRateDto[].class)));
		}

		List<ExchangeRateDto> exchangeRates = googleSheetsClient.getExchangeRates();
		cacheData(REDIS_KEY_EXCHANGE_RATES_PREFIX + ALL, exchangeRates);
		saveIndividualExchangeRates(exchangeRates);
		return exchangeRates;
	}

	@Override
	public ExchangeRateDto getExchangeRate(LocalDate date) {
		Optional<CacheData> optionalCacheData = cacheService.getKey(REDIS_KEY_EXCHANGE_RATES_PREFIX + date);
		if (optionalCacheData.isEmpty()) {
			List<ExchangeRateDto> exchangeRates = getExchangeRates();
			exchangeRates.stream().filter(exchangeRate -> exchangeRate.getDate().equals(date)).findFirst().ifPresent(exchangeRate -> {
				cacheData(REDIS_KEY_EXCHANGE_RATES_PREFIX + exchangeRate.getDate(), exchangeRate);
			});
			return exchangeRates.stream().filter(exchangeRate -> exchangeRate.getDate().equals(date)).findFirst().orElse(null);
		} else {
			String exchangeRateAsString = optionalCacheData.get().getValue();
			return jsonStringToObject(exchangeRateAsString, ExchangeRateDto.class);
		}
	}

	@Override
	public BigDecimal convertCurrencyToCzk(Currency currency, LocalDate date, BigDecimal amount) {
		ExchangeRateDto exchangeRate = getExchangeRate(date);
		return switch (currency.getCurrencyCode()) {
			case "CZK" -> amount;
			case "EUR" -> exchangeRate.getEurExchangeRate().multiply(amount);
			default -> throw new IllegalArgumentException("Currency " + currency + " is not supported");
		};
	}

	private void saveIndividualExchangeRates(List<ExchangeRateDto> exchangeRates) {
		for (ExchangeRateDto exchangeRateDto : exchangeRates) {
			cacheData(REDIS_KEY_EXCHANGE_RATES_PREFIX + exchangeRateDto.getDate(), exchangeRateDto);
		}
	}

	private <T> void cacheData(String key, T object) {
		String objectAsJsonString = objectToJsonString(object);
		CacheData cacheData = new CacheData(key, objectAsJsonString);
		cacheService.save(cacheData);
	}

	private <T> T jsonStringToObject(String content, Class<T> clazz) {
		try {
			return objectMapper.readValue(content, clazz);
		} catch (JsonProcessingException e) {
			log.error("Error while parsing json string from cache", e);
			return null;
		}
	}

	private <T> String objectToJsonString(T object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Error while writing object to json", e);
			return null;
		}
	}

}
