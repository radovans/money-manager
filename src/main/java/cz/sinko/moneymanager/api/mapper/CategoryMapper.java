package cz.sinko.moneymanager.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cz.sinko.moneymanager.api.response.CategoryDto;
import cz.sinko.moneymanager.repository.model.Category;

@Mapper()
public interface CategoryMapper {

	static CategoryMapper t() {
		return Mappers.getMapper(CategoryMapper.class);
	}

	CategoryDto mapCategory(Category source);

	List<CategoryDto> mapCategory(List<Category> source);

	Category map(CategoryDto categoryDto);
}
