package cz.sinko.moneymanager.api.controller;

import cz.sinko.moneymanager.api.dto.MortgageCreateDto;
import cz.sinko.moneymanager.api.dto.MortgageDto;
import cz.sinko.moneymanager.api.dto.MortgageStatisticsDto;
import cz.sinko.moneymanager.facade.MortgageFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cz.sinko.moneymanager.api.ApiUris.ROOT_URI_MORTGAGES;

@RestController
@RequestMapping(ROOT_URI_MORTGAGES)
@AllArgsConstructor
@Validated
@Slf4j
public class MortgageController {

    private final MortgageFacade mortgageFacade;

    @GetMapping
    public ResponseEntity<List<MortgageDto>> getMortgages() {
        log.info("Finding all mortgages.");
        return ResponseEntity.ok().body(mortgageFacade.getMortgages());
    }

    @GetMapping("/statistics")
    public ResponseEntity<MortgageStatisticsDto> getMortgageStatistics() {
        log.info("Getting mortgage statistics.");
        return ResponseEntity.ok().body(mortgageFacade.getStatistics());
    }

    @PostMapping
    public ResponseEntity<MortgageDto> createMortgage(@RequestBody MortgageCreateDto mortgageCreateDto) {
        log.info("Creating new Mortgage: '{}'.", mortgageCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mortgageFacade.createMortgage(mortgageCreateDto));
    }

}
