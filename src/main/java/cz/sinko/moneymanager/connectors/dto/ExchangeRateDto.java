package cz.sinko.moneymanager.connectors.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ExchangeRateDto {

	private LocalDate date;
	private BigDecimal eurExchangeRate;

}
