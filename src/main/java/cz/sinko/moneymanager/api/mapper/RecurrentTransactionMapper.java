package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.RecurrentTransactionDto;
import cz.sinko.moneymanager.repository.model.RecurrentTransaction;
import cz.sinko.moneymanager.repository.model.Transaction;

@Mapper(uses = { CategoryMapper.class, AccountMapper.class })
public interface RecurrentTransactionMapper {

	static RecurrentTransactionMapper t() {
		return Mappers.getMapper(RecurrentTransactionMapper.class);
	}

	List<RecurrentTransactionDto> map(List<RecurrentTransaction> source);

	@Mapping(target = "firstPayment", expression = "java(source.getFirstPayment().toString())")
	@Mapping(target = "frequency", expression = "java(source.getFrequency().toString())")
	@Mapping(target = "account", source = "account.name")
	@Mapping(target = "category", source = "category.name")
	@Mapping(target = "mainCategory", source = "mainCategory.name")
	RecurrentTransactionDto map(RecurrentTransaction source);

	@Mapping(target = "date", expression = "java(source.getFirstPayment().atYear(java.time.LocalDate.now().getYear()))")
	Transaction mapToTransaction(RecurrentTransaction source);

}