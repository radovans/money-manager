package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.AccountTransactionDto;
import cz.sinko.moneymanager.api.response.TransactionDto;
import cz.sinko.moneymanager.model.Transaction;

@Mapper(uses = { CategoryMapper.class })
public interface TransactionMapper {

	static TransactionMapper t() {
		return Mappers.getMapper(TransactionMapper.class);
	}

	@IterableMapping(qualifiedByName = "map")
	List<AccountTransactionDto> map(List<Transaction> source);

	@Named(value = "map")
	AccountTransactionDto map(Transaction source);

	@IterableMapping(qualifiedByName = "mapTransactionWithoutAccount")
	List<TransactionDto> mapTransactionWithoutAccount(List<Transaction> source);

	@Named(value = "mapTransactionWithoutAccount")
	TransactionDto mapTransactionWithoutAccount(Transaction source);

}
