package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.dto.AccountDto;
import cz.sinko.moneymanager.repository.model.Account;

@Mapper()
public interface AccountMapper {

	static AccountMapper t() {
		return Mappers.getMapper(AccountMapper.class);
	}

	AccountDto mapAccount(Account source);

	List<AccountDto> map(List<Account> source);

	Account map(AccountDto source);

}
