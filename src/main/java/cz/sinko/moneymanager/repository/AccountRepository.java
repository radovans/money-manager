package cz.sinko.moneymanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cz.sinko.moneymanager.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
}
