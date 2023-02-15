package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.RuleDto;
import cz.sinko.moneymanager.repository.model.Rule;

@Mapper(uses = { CategoryMapper.class })
public interface RuleMapper {

	static RuleMapper t() {
		return Mappers.getMapper(RuleMapper.class);
	}

	List<RuleDto> map(List<Rule> source);

	@Mapping(target = "category", source = "category.name")
	@Mapping(target = "mainCategory", source = "mainCategory.name")
	RuleDto map(Rule source);

}
