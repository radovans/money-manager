package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.SubcategoryDto;
import cz.sinko.moneymanager.repository.model.Subcategory;

@Mapper()
public interface SubcategoryMapper {

	static SubcategoryMapper t() {
		return Mappers.getMapper(SubcategoryMapper.class);
	}

	@Mapping(target = "category", source = "category.name")
	SubcategoryDto mapSubcategory(Subcategory source);

	List<SubcategoryDto> mapSubcategory(List<Subcategory> source);

	@Mapping(target = "category", ignore = true)
	Subcategory map(SubcategoryDto source);
}
