package cz.sinko.moneymanager.repository.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Mortgage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String name;

    private BigDecimal propertyValue;
    private String currency;
    private BigDecimal amount;
    private BigDecimal firstInterestPayment;
    @OneToMany(mappedBy = "mortgage", cascade = CascadeType.ALL)
    private List<Interest> interests;
    private LocalDate startDate;
    private Integer numberOfPayments;

}
