package cz.sinko.moneymanager.api.dto;

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
public class MortgageStatisticsDto {

    private BigDecimal propertyValuesInCzk;
    private String formattedPropertyValuesInCzk;
    private BigDecimal downPaymentsInCzk;
    private String formattedDownPaymentsInCzk;
    private BigDecimal amountInCzk;
    private String formattedAmountInCzk;
    private BigDecimal DTI;
    private BigDecimal LTV;

    public void setAmountInCzk(BigDecimal amountInCzk) {
        this.amountInCzk = amountInCzk;
        this.formattedAmountInCzk = FormatUtil.formatBigDecimal(amountInCzk);
    }

    public void setPropertyValuesInCzk(BigDecimal propertyValuesInCzk) {
        this.propertyValuesInCzk = propertyValuesInCzk;
        this.formattedPropertyValuesInCzk = FormatUtil.formatBigDecimal(propertyValuesInCzk);
    }

    public void setDownPaymentsInCzk(BigDecimal downPaymentsInCzk) {
        this.downPaymentsInCzk = downPaymentsInCzk;
        this.formattedDownPaymentsInCzk = FormatUtil.formatBigDecimal(downPaymentsInCzk);
    }

}