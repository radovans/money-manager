package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.dto.RuleDto;
import cz.sinko.moneymanager.repository.model.Rule;

@Mapper(uses = { CategoryMapper.class })
public interface RuleMapper {

	static RuleMapper t() {
		return Mappers.getMapper(RuleMapper.class);
	}

	@Mapping(target = "subcategory", source = "subcategory.name")
	@Mapping(target = "category", source = "category.name")
	RuleDto map(Rule source);

	List<RuleDto> map(List<Rule> source);

	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "category", ignore = true)
	Rule map(RuleDto source);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "subcategory", ignore = true)
	@Mapping(target = "category", ignore = true)
	void update(@MappingTarget Rule entity, RuleDto updateEntity);
}
