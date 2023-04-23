package cz.sinko.moneymanager.service;

import cz.sinko.moneymanager.api.dto.ActualStatisticsDto;
import cz.sinko.moneymanager.api.dto.InterestDto;
import cz.sinko.moneymanager.api.dto.MortgageDto;
import cz.sinko.moneymanager.api.dto.MortgageMonthStatisticsDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class MortgageCalculator {

    /**
     * Calculates the payment for an annuity with the given interest rate
     * and number of periods.
     *
     * @param rate            the interest rate as a decimal value
     * @param numberOfPeriods the number of periods (payments) to be made
     * @param balance         the current value of the annuity
     * @return the monthly payment as a BigDecimal value
     */
    public static BigDecimal calculatePayment(BigDecimal rate, int numberOfPeriods, BigDecimal balance) {
        BigDecimal monthlyRate = calculateMonthlyInterestRate(rate);

        // Calculate the monthly payment using the formula:
        // M = P * (r * (1 + r)^n) / ((1 + r)^n - 1)
        BigDecimal numerator = monthlyRate.multiply(BigDecimal.ONE.add(monthlyRate).pow(numberOfPeriods));
        BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(numberOfPeriods).subtract(BigDecimal.ONE);
        return balance.divide(denominator.divide(numerator, 10, RoundingMode.UP), 2, RoundingMode.UP);
    }

    /**
     * Calculates the monthly interest rate for a given annual rate.
     *
     * @param rate the annual interest rate as a decimal value
     * @return the monthly interest rate as a BigDecimal value
     */
    private static BigDecimal calculateMonthlyInterestRate(BigDecimal rate) {
        return rate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the interest for a given rate and balance.
     *
     * @param rate    the interest rate as a decimal value
     * @param balance the current value of the annuity
     * @return the interest as a BigDecimal value
     */
    public static BigDecimal calculateInterest(BigDecimal rate, BigDecimal balance) {
        BigDecimal monthlyRate = calculateMonthlyInterestRate(rate);
        return balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the principal for a given payment and interest.
     *
     * @param payment  the payment as a BigDecimal value
     * @param interest the interest as a BigDecimal value
     * @return the principal as a BigDecimal value
     */
    public static BigDecimal calculatePrincipal(BigDecimal payment, BigDecimal interest) {
        return payment.subtract(interest).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the payment calendar for a given mortgage.
     *
     * @param mortgageDto the mortgage to calculate the payment calendar for
     * @return the payment calendar as a list of MortgageMonthStatisticsDto objects
     */
    public static List<MortgageMonthStatisticsDto> calculatePaymentCalendar(MortgageDto mortgageDto) {
        BigDecimal remainingBalance = mortgageDto.getAmount();
        int remainingPeriods = mortgageDto.getNumberOfPayments();
        List<MortgageMonthStatisticsDto> paymentCalendar = new ArrayList<>();

        // first interest payment
        remainingPeriods--;
        paymentCalendar.add(MortgageMonthStatisticsDto.builder()
                .yearMonth(YearMonth.from(mortgageDto.getStartDate()))
                .principal(BigDecimal.ZERO)
                .interest(mortgageDto.getFirstInterestPayment())
                .payment(mortgageDto.getFirstInterestPayment())
                .remainingBalance(remainingBalance)
                .build());

        // rest of the payments
        for (InterestDto interestDto : mortgageDto.getInterests()) {
            for (int i = 0; i < interestDto.getNumberOfPayments(); i++) {
                BigDecimal payment = calculatePayment(interestDto.getInterestRate(), remainingPeriods, remainingBalance);
                BigDecimal interest = calculateInterest(interestDto.getInterestRate(), remainingBalance);
                BigDecimal principal = calculatePrincipal(payment, interest);
                remainingPeriods--;
                remainingBalance = remainingBalance.subtract(principal);
                paymentCalendar.add(MortgageMonthStatisticsDto.builder()
                        .yearMonth(YearMonth.from(interestDto.getStartDate().plusMonths(i).plusMonths(1)))
                        .principal(principal)
                        .interest(interest)
                        .payment(payment)
                        .remainingBalance(remainingBalance)
                        .build());
            }
        }
        return paymentCalendar;
    }

    public static ActualStatisticsDto calculateActualStatistics(MortgageDto mortgageDto) {
        List<MortgageMonthStatisticsDto> paymentCalendar = mortgageDto.getPaymentCalendar();
        ActualStatisticsDto actualStatisticsDto = new ActualStatisticsDto();
        actualStatisticsDto.setInterestPaid(paymentCalendar.stream()
                .filter(monthStatisticsDto -> monthStatisticsDto.getYearMonth().isBefore(YearMonth.now()))
                .map(MortgageMonthStatisticsDto::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        actualStatisticsDto.setPrincipalPaid(paymentCalendar.stream()
                .filter(monthStatisticsDto -> monthStatisticsDto.getYearMonth().isBefore(YearMonth.now()))
                .map(MortgageMonthStatisticsDto::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        actualStatisticsDto.setInterestPaidInCzk(paymentCalendar.stream()
                .filter(monthStatisticsDto -> monthStatisticsDto.getYearMonth().isBefore(YearMonth.now()))
                .map(MortgageMonthStatisticsDto::getInterestInCzk)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        actualStatisticsDto.setPrincipalPaidInCzk(paymentCalendar.stream()
                .filter(monthStatisticsDto -> monthStatisticsDto.getYearMonth().isBefore(YearMonth.now()))
                .map(MortgageMonthStatisticsDto::getPrincipalInCzk)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return actualStatisticsDto;
    }
}
