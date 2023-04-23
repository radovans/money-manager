package cz.sinko.moneymanager.service;

import cz.sinko.moneymanager.api.dto.MortgageCreateDto;
import cz.sinko.moneymanager.api.mapper.MortgageMapper;
import cz.sinko.moneymanager.repository.MortgageRepository;
import cz.sinko.moneymanager.repository.model.Mortgage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class MortgageService {

    private final MortgageRepository mortgageRepository;

    public List<Mortgage> find(Sort sort) {
        return mortgageRepository.findAll(sort);
    }

    public Mortgage createMortgage(MortgageCreateDto mortgageCreateDto) {
        Mortgage mortgage = MortgageMapper.t().mapToMortgage(mortgageCreateDto);
        mortgage.getInterests().forEach(interest -> interest.setMortgage(mortgage));
        return mortgageRepository.save(mortgage);
    }

}
