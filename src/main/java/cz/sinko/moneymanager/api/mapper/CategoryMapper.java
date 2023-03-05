package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.api.response.MainCategoryDto;
import cz.sinko.moneymanager.repository.model.Category;
import cz.sinko.moneymanager.repository.model.MainCategory;

@Mapper()
public interface CategoryMapper {

	static CategoryMapper t() {
		return Mappers.getMapper(CategoryMapper.class);
	}

	MainCategoryDto mapMainCategory(MainCategory source);

	List<MainCategoryDto> mapMainCategory(List<MainCategory> source);

	CategoryDto mapCategory(Category source);

	List<CategoryDto> mapCategory(List<Category> source);

}
