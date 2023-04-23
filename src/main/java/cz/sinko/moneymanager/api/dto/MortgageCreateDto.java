package cz.sinko.moneymanager.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MortgageCreateDto {

    @NotBlank(message = "Name may not be blank")
    private String name;
    private BigDecimal propertyValue;
    private String currency;
    private BigDecimal amount;
    private BigDecimal firstInterestPayment;
    private List<InterestDto> interests;
    private LocalDate startDate;
    private int numberOfPayments;

}