package cz.sinko.moneymanager.api.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.AccountDto;
import cz.sinko.moneymanager.api.response.AccountWithTransactionsDto;
import cz.sinko.moneymanager.model.Account;

@Mapper(imports = { Collections.class})
public interface AccountMapper {

	static AccountMapper t() {
		return Mappers.getMapper(AccountMapper.class);
	}

	@IterableMapping(qualifiedByName = "mapAccount")
	List<AccountDto> map(List<Account> source);

	@Named(value = "mapAccount")
	AccountDto mapAccount(Account source);

	AccountWithTransactionsDto mapAccountWithTransactions(Account source);

}
