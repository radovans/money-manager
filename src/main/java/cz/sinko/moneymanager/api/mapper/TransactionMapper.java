package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import cz.sinko.moneymanager.api.response.AccountTransactionDto;
import cz.sinko.moneymanager.api.response.TransactionDto;
import cz.sinko.moneymanager.repository.model.Transaction;

@Mapper(uses = { CategoryMapper.class })
public interface TransactionMapper {

	static TransactionMapper t() {
		return Mappers.getMapper(TransactionMapper.class);
	}

	@IterableMapping(qualifiedByName = "map")
	List<AccountTransactionDto> map(Page<Transaction> source);

	@Named(value = "map")
	@Mapping(target = "account", source = "account.name")
	@Mapping(target = "subcategory", source = "subcategory.name")
	@Mapping(target = "category", source = "category.name")
	AccountTransactionDto map(Transaction source);

	@IterableMapping(qualifiedByName = "mapTransactionWithoutAccount")
	List<TransactionDto> mapTransactionWithoutAccount(List<Transaction> source);

	@Named(value = "mapTransactionWithoutAccount")
	@Mapping(target = "subcategory", source = "subcategory.name")
	@Mapping(target = "category", source = "category.name")
	@Mapping(target = "account", source = "account.name")
	TransactionDto mapTransactionWithoutAccount(Transaction source);

	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "account", ignore = true)
	Transaction map(TransactionDto source);
}
