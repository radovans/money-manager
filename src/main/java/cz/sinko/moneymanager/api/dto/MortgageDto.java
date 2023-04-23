package cz.sinko.moneymanager.api.dto;

import cz.sinko.moneymanager.service.FormatUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MortgageDto {

    private Long id;

    @NotBlank(message = "Name may not be blank")
    private String name;

    private BigDecimal propertyValue;
    private BigDecimal propertyValueInCzk;
    private String formattedPropertyValueInCzk;
    private BigDecimal downPayment;
    private BigDecimal downPaymentInCzk;
    private String formattedDownPaymentInCzk;
    private String currency;
    private BigDecimal amount;
    private BigDecimal amountInCzk;
    private String formattedAmountInCzk;
    private BigDecimal firstInterestPayment;
    private List<InterestDto> interests;
    private LocalDate startDate;
    private Integer numberOfPayments;
    private ActualStatisticsDto actualStatistics;
    private List<MortgageMonthStatisticsDto> paymentCalendar;

    public void setAmountInCzk(BigDecimal amountInCzk) {
        this.amountInCzk = amountInCzk.setScale(2, RoundingMode.HALF_UP);
        this.formattedAmountInCzk = FormatUtil.formatBigDecimal(amountInCzk);
    }

    public void setPropertyValueInCzk(BigDecimal propertyValueInCzk) {
        this.propertyValueInCzk = propertyValueInCzk.setScale(2, RoundingMode.HALF_UP);
        this.formattedPropertyValueInCzk = FormatUtil.formatBigDecimal(propertyValueInCzk);
    }

    public void setDownPaymentInCzk(BigDecimal downPaymentInCzk) {
        this.downPaymentInCzk = downPaymentInCzk.setScale(2, RoundingMode.HALF_UP);
        this.formattedDownPaymentInCzk = FormatUtil.formatBigDecimal(downPaymentInCzk);
    }

}