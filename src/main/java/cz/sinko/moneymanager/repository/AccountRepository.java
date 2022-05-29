package cz.sinko.moneymanager.repository;

import org.springframework.data.repository.CrudRepository;

import cz.sinko.moneymanager.model.Account;

public interface AccountRepository extends CrudRepository <Account, Integer> {
}
