package cz.sinko.moneymanager.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.api.response.MainCategoryDto;
import cz.sinko.moneymanager.model.Category;
import cz.sinko.moneymanager.model.MainCategory;

@Mapper()
public interface CategoryMapper {

	static CategoryMapper t() {
		return Mappers.getMapper(CategoryMapper.class);
	}

	MainCategoryDto mapMainCategory(MainCategory source);

	CategoryDto mapCategory(Category source);

}
