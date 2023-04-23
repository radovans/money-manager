package cz.sinko.moneymanager.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.sinko.moneymanager.service.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MortgageMonthStatisticsDto {

    private YearMonth yearMonth;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal principal;
    private BigDecimal principalInCzk;
    private String formattedPrincipalInCzk;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal interest;
    private BigDecimal interestInCzk;
    private String formattedInterestInCzk;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal payment;
    private BigDecimal paymentInCzk;
    private String formattedPaymentInCzk;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal remainingBalance;
    private BigDecimal remainingBalanceInCzk;
    private String formattedRemainingBalanceInCzk;
    private BigDecimal DTI;
    private BigDecimal LTV;

    public void setPrincipalInCzk(BigDecimal principalInCzk) {
        this.principalInCzk = principalInCzk.setScale(2, RoundingMode.HALF_UP);
        this.formattedPrincipalInCzk = FormatUtil.formatBigDecimal(principalInCzk);
    }

    public void setInterestInCzk(BigDecimal interestInCzk) {
        this.interestInCzk = interestInCzk.setScale(2, RoundingMode.HALF_UP);
        this.formattedInterestInCzk = FormatUtil.formatBigDecimal(interestInCzk);
    }

    public void setPaymentInCzk(BigDecimal paymentInCzk) {
        this.paymentInCzk = paymentInCzk.setScale(2, RoundingMode.HALF_UP);
        this.formattedPaymentInCzk = FormatUtil.formatBigDecimal(paymentInCzk);
    }

    public void setRemainingBalanceInCzk(BigDecimal remainingBalanceInCzk) {
        this.remainingBalanceInCzk = remainingBalanceInCzk.setScale(2, RoundingMode.HALF_UP);
        this.formattedRemainingBalanceInCzk = FormatUtil.formatBigDecimal(remainingBalanceInCzk);
    }

}
