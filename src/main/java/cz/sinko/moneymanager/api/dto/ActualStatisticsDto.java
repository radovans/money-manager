package cz.sinko.moneymanager.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.sinko.moneymanager.service.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ActualStatisticsDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal interestPaid;
    private BigDecimal interestPaidInCzk;
    private String formattedInterestPaidInCzk;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal principalPaid;
    private BigDecimal principalPaidInCzk;
    private String formattedPrincipalPaidInCzk;
    private BigDecimal DTI;
    private BigDecimal LTV;

    public void setInterestPaidInCzk(BigDecimal interestPaidInCzk) {
        this.interestPaidInCzk = interestPaidInCzk;
        this.formattedInterestPaidInCzk = FormatUtil.formatBigDecimal(interestPaidInCzk);
    }

    public void setPrincipalPaidInCzk(BigDecimal principalPaidInCzk) {
        this.principalPaidInCzk = principalPaidInCzk;
        this.formattedPrincipalPaidInCzk = FormatUtil.formatBigDecimal(principalPaidInCzk);
    }

}
