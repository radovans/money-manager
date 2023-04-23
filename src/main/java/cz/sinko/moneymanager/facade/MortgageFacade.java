package cz.sinko.moneymanager.facade;

import cz.sinko.moneymanager.api.dto.*;
import cz.sinko.moneymanager.api.mapper.MortgageMapper;
import cz.sinko.moneymanager.connectors.service.ExchangeService;
import cz.sinko.moneymanager.repository.model.Mortgage;
import cz.sinko.moneymanager.service.MortgageCalculator;
import cz.sinko.moneymanager.service.MortgageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class MortgageFacade {

    public static final int NUMBER_OF_MONTHS_IN_YEAR = 12;
    public static final int MONTHLY_INCOME = 10000;
    private final MortgageService mortgageService;
    private final ExchangeService exchangeService;

    public List<MortgageDto> getMortgages() {
        List<MortgageDto> mortgageDtos = getMortgageDtos();
        MortgageDto totalMortgageDto = calculateTotalStatistics(mortgageDtos);
        mortgageDtos.add(totalMortgageDto);
        return mortgageDtos;
    }

    private MortgageDto calculateTotalStatistics(List<MortgageDto> mortgageDtos) {
        if (mortgageDtos.isEmpty()) {
            return null;
        }

        MortgageDto totalMortgageDto = new MortgageDto();
        totalMortgageDto.setName("Total");
        ActualStatisticsDto totalActualStatisticsDto = new ActualStatisticsDto();
        totalActualStatisticsDto.setInterestPaidInCzk(mortgageDtos.stream().map(MortgageDto::getActualStatistics).map(ActualStatisticsDto::getInterestPaidInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
        totalActualStatisticsDto.setPrincipalPaidInCzk(mortgageDtos.stream().map(MortgageDto::getActualStatistics).map(ActualStatisticsDto::getPrincipalPaidInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
        totalMortgageDto.setActualStatistics(totalActualStatisticsDto);

        YearMonth firstMonth = mortgageDtos.stream()
                .flatMap(mortgageDto -> mortgageDto.getPaymentCalendar().stream())
                .map(MortgageMonthStatisticsDto::getYearMonth)
                .min(YearMonth::compareTo).orElse(null);

        MortgageStatisticsDto statistics = getStatistics(mortgageDtos);

        if (firstMonth != null) {
            List<MortgageMonthStatisticsDto> paymentCalendar = new ArrayList<>();
            while (firstMonth.isBefore(YearMonth.now())) {
                YearMonth yearMonth = firstMonth;
                MortgageMonthStatisticsDto mortgageMonthStatisticsDto = new MortgageMonthStatisticsDto();
                mortgageMonthStatisticsDto.setYearMonth(yearMonth);
                mortgageMonthStatisticsDto.setPrincipalInCzk(mortgageDtos.stream()
                        .flatMap(mortgageDto -> mortgageDto.getPaymentCalendar().stream())
                        .filter(mortgageMonthStatisticsDto1 -> mortgageMonthStatisticsDto1.getYearMonth().equals(yearMonth))
                        .map(MortgageMonthStatisticsDto::getPrincipalInCzk)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                mortgageMonthStatisticsDto.setInterestInCzk(mortgageDtos.stream()
                        .flatMap(mortgageDto -> mortgageDto.getPaymentCalendar().stream())
                        .filter(mortgageMonthStatisticsDto1 -> mortgageMonthStatisticsDto1.getYearMonth().equals(yearMonth))
                        .map(MortgageMonthStatisticsDto::getInterestInCzk)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                mortgageMonthStatisticsDto.setPaymentInCzk(mortgageDtos.stream()
                        .flatMap(mortgageDto -> mortgageDto.getPaymentCalendar().stream())
                        .filter(mortgageMonthStatisticsDto1 -> mortgageMonthStatisticsDto1.getYearMonth().equals(yearMonth))
                        .map(MortgageMonthStatisticsDto::getPaymentInCzk)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                mortgageMonthStatisticsDto.setRemainingBalanceInCzk(mortgageDtos.stream()
                        .flatMap(mortgageDto -> mortgageDto.getPaymentCalendar().stream())
                        .filter(mortgageMonthStatisticsDto1 -> mortgageMonthStatisticsDto1.getYearMonth().equals(yearMonth))
                        .map(MortgageMonthStatisticsDto::getRemainingBalanceInCzk)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
                mortgageMonthStatisticsDto.setLTV(mortgageMonthStatisticsDto.getRemainingBalanceInCzk().multiply(BigDecimal.valueOf(100)).divide(statistics.getPropertyValuesInCzk(), 2, RoundingMode.HALF_UP));
                mortgageMonthStatisticsDto.setDTI(mortgageMonthStatisticsDto.getRemainingBalanceInCzk().divide(new BigDecimal(MONTHLY_INCOME), 2, RoundingMode.HALF_UP).divide(new BigDecimal(NUMBER_OF_MONTHS_IN_YEAR), 2, RoundingMode.HALF_UP));
                paymentCalendar.add(mortgageMonthStatisticsDto);
                firstMonth = firstMonth.plusMonths(1);
            }
            totalMortgageDto.setPaymentCalendar(paymentCalendar);
        }
        return totalMortgageDto;
    }

    public MortgageDto createMortgage(MortgageCreateDto mortgageCreateDto) {
        return MortgageMapper.t().mapToMortgageDto(mortgageService.createMortgage(mortgageCreateDto));
    }

    public MortgageStatisticsDto getStatistics() {
        List<MortgageDto> mortgageDtos = getMortgageDtos();
        if (mortgageDtos.isEmpty()) {
            return null;
        }
        return getStatistics(mortgageDtos);
    }

    public MortgageStatisticsDto getStatistics(List<MortgageDto> mortgageDtos) {
        MortgageStatisticsDto mortgageStatisticsDto = new MortgageStatisticsDto();
        mortgageStatisticsDto.setPropertyValuesInCzk(mortgageDtos.stream().map(MortgageDto::getPropertyValueInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
        mortgageStatisticsDto.setAmountInCzk(mortgageDtos.stream().map(MortgageDto::getAmountInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
        mortgageStatisticsDto.setDownPaymentsInCzk(mortgageDtos.stream().map(MortgageDto::getDownPaymentInCzk).reduce(BigDecimal.ZERO, BigDecimal::add));
        mortgageStatisticsDto.setLTV(mortgageStatisticsDto.getDownPaymentsInCzk().multiply(BigDecimal.valueOf(100).divide(mortgageStatisticsDto.getPropertyValuesInCzk(), 2, RoundingMode.HALF_UP)));
        mortgageStatisticsDto.setDTI(mortgageStatisticsDto.getAmountInCzk().divide(new BigDecimal(MONTHLY_INCOME), 2, RoundingMode.HALF_UP).divide(new BigDecimal(NUMBER_OF_MONTHS_IN_YEAR), 2, RoundingMode.HALF_UP));
        return mortgageStatisticsDto;
    }

    private List<MortgageDto> getMortgageDtos() {
        List<Mortgage> mortgages = mortgageService.find(Sort.by("id").ascending());
        List<MortgageDto> mortgageDtos = MortgageMapper.t().mapToMortgageDtos(mortgages);
        mortgageDtos.forEach(mortgageDto -> mortgageDto.setDownPayment(mortgageDto.getPropertyValue().subtract(mortgageDto.getAmount())));
        mortgageDtos.forEach(mortgageDto -> mortgageDto.setPaymentCalendar(MortgageCalculator.calculatePaymentCalendar(mortgageDto)));
        mortgageDtos.forEach(this::convertCurrency);
        mortgageDtos.forEach(mortgageDto -> {
            ActualStatisticsDto actualStatisticsDto = MortgageCalculator.calculateActualStatistics(mortgageDto);
            mortgageDto.setActualStatistics(actualStatisticsDto);
        });
        return mortgageDtos;
    }

    private void convertCurrency(MortgageDto mortgageDto) {
        Currency currency = Currency.getInstance(mortgageDto.getCurrency());
        LocalDate mortgageStartDate = mortgageDto.getStartDate();

        if (mortgageDto.getAmount() != null) {
            BigDecimal convertedEurToCzk =
                    exchangeService.convertCurrencyToCzk(currency, mortgageStartDate, mortgageDto.getAmount());
            mortgageDto.setAmountInCzk(convertedEurToCzk);
        }

        if (mortgageDto.getDownPayment() != null) {
            BigDecimal convertedEurToCzk =
                    exchangeService.convertCurrencyToCzk(currency, mortgageStartDate, mortgageDto.getDownPayment());
            mortgageDto.setDownPaymentInCzk(convertedEurToCzk);
        }

        if (mortgageDto.getPropertyValue() != null) {
            BigDecimal convertedEurToCzk =
                    exchangeService.convertCurrencyToCzk(currency, mortgageStartDate, mortgageDto.getPropertyValue());
            mortgageDto.setPropertyValueInCzk(convertedEurToCzk);
        }

        mortgageDto.getPaymentCalendar().forEach(mortgageMonthStatisticsDto -> {
            LocalDate date = mortgageMonthStatisticsDto.getYearMonth().atDay(20).isBefore(LocalDate.now()) ? mortgageMonthStatisticsDto.getYearMonth().atDay(20) : LocalDate.now().minusDays(1);
            if (mortgageMonthStatisticsDto.getPayment() != null) {
                BigDecimal convertedEurToCzk =
                        exchangeService.convertCurrencyToCzk(currency, date, mortgageMonthStatisticsDto.getPayment());
                mortgageMonthStatisticsDto.setPaymentInCzk(convertedEurToCzk);
            }
            if (mortgageMonthStatisticsDto.getInterest() != null) {
                BigDecimal convertedEurToCzk =
                        exchangeService.convertCurrencyToCzk(currency, date, mortgageMonthStatisticsDto.getInterest());
                mortgageMonthStatisticsDto.setInterestInCzk(convertedEurToCzk);
            }
            if (mortgageMonthStatisticsDto.getPrincipal() != null) {
                BigDecimal convertedEurToCzk =
                        exchangeService.convertCurrencyToCzk(currency, date, mortgageMonthStatisticsDto.getPrincipal());
                mortgageMonthStatisticsDto.setPrincipalInCzk(convertedEurToCzk);
            }
            if (mortgageMonthStatisticsDto.getRemainingBalance() != null) {
                BigDecimal convertedEurToCzk =
                        exchangeService.convertCurrencyToCzk(currency, date, mortgageMonthStatisticsDto.getRemainingBalance());
                mortgageMonthStatisticsDto.setRemainingBalanceInCzk(convertedEurToCzk);
            }
        });

    }

}
