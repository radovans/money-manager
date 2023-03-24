package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import cz.sinko.moneymanager.api.dto.AccountTransactionDto;
import cz.sinko.moneymanager.api.dto.TransactionDto;
import cz.sinko.moneymanager.repository.model.Transaction;

@Mapper(uses = { CategoryMapper.class })
public interface TransactionMapper {

	static TransactionMapper t() {
		return Mappers.getMapper(TransactionMapper.class);
	}

	@Mapping(target = "subcategory", source = "subcategory.name")
	@Mapping(target = "category", source = "category.name")
	@Mapping(target = "account", source = "account.name")
	TransactionDto map(Transaction source);

	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "category", ignore = true)
	@Mapping(target = "account", ignore = true)
	Transaction map(TransactionDto source);

	@Mapping(target = "account", source = "account.name")
	@Mapping(target = "subcategory", source = "subcategory.name")
	@Mapping(target = "category", source = "category.name")
	AccountTransactionDto mapToAccountTransactionDto(Transaction source);

	List<AccountTransactionDto> mapToAccountTransactionDtoList(Page<Transaction> source);

}
