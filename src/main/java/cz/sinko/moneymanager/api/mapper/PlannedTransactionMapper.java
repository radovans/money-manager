package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.PlannedTransactionDto;
import cz.sinko.moneymanager.repository.model.PlannedTransaction;
import cz.sinko.moneymanager.repository.model.Transaction;

@Mapper(uses = { CategoryMapper.class, AccountMapper.class })
public interface PlannedTransactionMapper {

	static PlannedTransactionMapper t() {
		return Mappers.getMapper(PlannedTransactionMapper.class);
	}

	List<PlannedTransactionDto> map(List<PlannedTransaction> source);

	@Mapping(target = "account", source = "account.name")
	@Mapping(target = "subcategory", source = "subcategory.name")
	@Mapping(target = "category", source = "category.name")
	PlannedTransactionDto map(PlannedTransaction source);

	List<Transaction> mapToTransaction(List<PlannedTransaction> source);

	@Mapping(target = "date", expression = "java(java.time.LocalDate.of(java.time.LocalDate.now().getYear(), java.time.LocalDate.now().getMonth(), source.getDayOfMonth()))")
	Transaction mapToTransaction(PlannedTransaction source);

}
