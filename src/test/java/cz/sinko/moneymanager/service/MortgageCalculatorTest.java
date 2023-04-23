package cz.sinko.moneymanager.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MortgageCalculatorTest {

    public static final String INTEREST_RATE = "3.00";
    public static final int NUMBER_OF_PERIODS = 48;
    public static final String LOAN_AMOUNT = "30000";
    public static final String REMAINING_BALANCE_AFTER_FIRST_PAYMENT = "29410.97";
    public static final String LAST_PERIOD_BALANCE = "662.36";

    @Test
    void calculateFirstPeriodPayment() {
        BigDecimal payment = MortgageCalculator.calculatePayment(new BigDecimal(INTEREST_RATE), NUMBER_OF_PERIODS, new BigDecimal(LOAN_AMOUNT));

        assertThat(payment).isEqualTo(new BigDecimal("664.03"));
    }

    @Test
    void calculateFirstPeriodInterest() {
        BigDecimal interest = MortgageCalculator.calculateInterest(new BigDecimal(INTEREST_RATE), new BigDecimal(LOAN_AMOUNT));

        assertThat(interest).isEqualTo(new BigDecimal("75.00"));
    }

    @Test
    void calculateFirstPeriodPrincipal() {
        BigDecimal payment = MortgageCalculator.calculatePayment(new BigDecimal(INTEREST_RATE), NUMBER_OF_PERIODS, new BigDecimal(LOAN_AMOUNT));
        BigDecimal interest = MortgageCalculator.calculateInterest(new BigDecimal(INTEREST_RATE), new BigDecimal(LOAN_AMOUNT));
        BigDecimal principal = MortgageCalculator.calculatePrincipal(payment, interest);

        assertThat(principal).isEqualTo(new BigDecimal("589.03"));
    }

    @Test
    void calculateSecondPeriod() {
        BigDecimal payment = MortgageCalculator.calculatePayment(new BigDecimal(INTEREST_RATE), NUMBER_OF_PERIODS - 1, new BigDecimal(REMAINING_BALANCE_AFTER_FIRST_PAYMENT));
        BigDecimal interest = MortgageCalculator.calculateInterest(new BigDecimal(INTEREST_RATE), new BigDecimal(REMAINING_BALANCE_AFTER_FIRST_PAYMENT));
        BigDecimal principal = MortgageCalculator.calculatePrincipal(payment, interest);

        assertThat(payment).isEqualTo(new BigDecimal("664.03"));
        assertThat(interest).isEqualTo(new BigDecimal("73.53"));
        assertThat(principal).isEqualTo(new BigDecimal("590.50"));
    }

    @Test
    void calculateLastPeriod() {
        BigDecimal payment = MortgageCalculator.calculatePayment(new BigDecimal(INTEREST_RATE), 1, new BigDecimal(LAST_PERIOD_BALANCE));
        BigDecimal interest = MortgageCalculator.calculateInterest(new BigDecimal(INTEREST_RATE), new BigDecimal(LAST_PERIOD_BALANCE));
        BigDecimal principal = MortgageCalculator.calculatePrincipal(payment, interest);

        assertThat(payment).isEqualTo(new BigDecimal("664.02"));
        assertThat(interest).isEqualTo(new BigDecimal("1.66"));
        assertThat(principal).isEqualTo(new BigDecimal("662.36"));
    }

}