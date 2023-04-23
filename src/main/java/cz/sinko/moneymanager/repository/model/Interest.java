package cz.sinko.moneymanager.repository.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal interestRate;
    private LocalDate startDate;
    private Integer numberOfPayments;

    @ManyToOne
    private Mortgage mortgage;

}
