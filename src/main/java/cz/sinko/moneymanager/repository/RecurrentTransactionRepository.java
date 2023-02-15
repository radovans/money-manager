package cz.sinko.moneymanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.repository.model.Frequency;
import cz.sinko.moneymanager.repository.model.RecurrentTransaction;

@Repository
public interface RecurrentTransactionRepository extends JpaRepository<RecurrentTransaction, Long> {

	List<RecurrentTransaction> findByFrequency(Frequency frequency);

}
