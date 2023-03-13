package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.SubcategoryDto;
import cz.sinko.moneymanager.repository.model.Subcategory;

@Mapper()
public interface SubcategoryMapper {

	static SubcategoryMapper t() {
		return Mappers.getMapper(SubcategoryMapper.class);
	}

	SubcategoryDto mapSubcategory(Subcategory source);

	List<SubcategoryDto> mapSubcategory(List<Subcategory> source);

}
