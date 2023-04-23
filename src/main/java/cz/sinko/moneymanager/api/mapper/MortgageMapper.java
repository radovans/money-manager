package cz.sinko.moneymanager.api.mapper;

import cz.sinko.moneymanager.api.dto.InterestDto;
import cz.sinko.moneymanager.api.dto.MortgageCreateDto;
import cz.sinko.moneymanager.api.dto.MortgageDto;
import cz.sinko.moneymanager.repository.model.Interest;
import cz.sinko.moneymanager.repository.model.Mortgage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper()
public interface MortgageMapper {

    static MortgageMapper t() {
        return Mappers.getMapper(MortgageMapper.class);
    }

    MortgageDto mapToMortgageDto(Mortgage source);

    List<MortgageDto> mapToMortgageDtos(List<Mortgage> source);

    Mortgage mapToMortgage(MortgageCreateDto source);

    Interest mapToInterest(InterestDto source);

    InterestDto mapToInterestDto(Interest source);

}
