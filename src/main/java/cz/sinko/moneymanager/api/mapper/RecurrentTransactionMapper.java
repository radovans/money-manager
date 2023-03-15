package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.dto.RecurrentTransactionDto;
import cz.sinko.moneymanager.repository.model.RecurrentTransaction;
import cz.sinko.moneymanager.repository.model.Transaction;

@Mapper(uses = { CategoryMapper.class, AccountMapper.class })
public interface RecurrentTransactionMapper {

	static RecurrentTransactionMapper t() {
		return Mappers.getMapper(RecurrentTransactionMapper.class);
	}

	@Mapping(target = "firstPayment", expression = "java(source.getFirstPayment().toString())")
	@Mapping(target = "frequency", expression = "java(source.getFrequency().toString())")
	@Mapping(target = "account", source = "account.name")
	@Mapping(target = "subcategory", source = "subcategory.name")
	@Mapping(target = "category", source = "category.name")
	RecurrentTransactionDto map(RecurrentTransaction source);

	List<RecurrentTransactionDto> map(List<RecurrentTransaction> source);

	@Mapping(target = "date", expression = "java(source.getFirstPayment().atYear(java.time.LocalDate.now().getYear()))")
	Transaction mapToTransaction(RecurrentTransaction source);

}
