package cz.sinko.moneymanager.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterestDto {

    private BigDecimal interestRate;
    private LocalDate startDate;
    private int numberOfPayments;

}
