package cz.sinko.moneymanager.api.mapper;

import java.util.Collections;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.AccountDto;
import cz.sinko.moneymanager.api.response.AccountWithTransactionsDto;
import cz.sinko.moneymanager.api.response.IncomeExpenseStatementDto;
import cz.sinko.moneymanager.repository.model.Account;

@Mapper(imports = { Collections.class })
public abstract class AccountMapper {

	static AccountMapper t() {
		return Mappers.getMapper(AccountMapper.class);
	}

	@IterableMapping(qualifiedByName = "mapAccount")
	public abstract List<AccountDto> map(List<Account> source);

	@Named(value = "mapAccount")
	public AccountDto mapAccount(Account source) {
		AccountDto accountDto = new AccountDto();
		accountDto.setId(source.getId());
		accountDto.setName(source.getName());
		if (source.getNumberOfTransactions() != null) {
			accountDto.setNumberOfTransactions(source.getNumberOfTransactions());
		}
		if (source.getBalance() != null && source.getIncome() != null && source.getExpense() != null) {
			IncomeExpenseStatementDto incomeExpenseStatement = new IncomeExpenseStatementDto(
					source.getBalance(), source.getIncome(), source.getExpense());
			accountDto.setIncomeExpenseStatement(incomeExpenseStatement);
		}
		return accountDto;
	}

	public AccountWithTransactionsDto mapAccountWithTransactions(Account source) {
		AccountWithTransactionsDto accountWithTransactionsDto = new AccountWithTransactionsDto();
		accountWithTransactionsDto.setId(source.getId());
		accountWithTransactionsDto.setName(source.getName());
		accountWithTransactionsDto.setNumberOfTransactions(source.getNumberOfTransactions());
		IncomeExpenseStatementDto incomeExpenseStatement = new IncomeExpenseStatementDto(
				source.getBalance(), source.getIncome(), source.getExpense());
		accountWithTransactionsDto.setIncomeExpenseStatement(incomeExpenseStatement);
		return accountWithTransactionsDto;
	}

	public Account map(AccountDto source) {
		Account account = new Account();
		account.setName(source.getName());
		return account;
	}

}
