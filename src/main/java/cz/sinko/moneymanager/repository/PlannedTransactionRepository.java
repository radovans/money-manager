package cz.sinko.moneymanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.repository.model.PlannedTransaction;

@Repository
public interface PlannedTransactionRepository extends JpaRepository<PlannedTransaction, Long> {

}
