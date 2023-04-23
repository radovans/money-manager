package cz.sinko.moneymanager.repository;

import cz.sinko.moneymanager.repository.model.Mortgage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageRepository extends JpaRepository<Mortgage, Long> {

}
