package cz.sinko.moneymanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.repository.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findByName(String account);
}
