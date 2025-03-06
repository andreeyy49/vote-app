package voteapp.geostorageservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.aop.LoggingLevel;
import voteapp.geostorageservice.dto.CityDto;
import voteapp.geostorageservice.dto.CountryDto;
import voteapp.geostorageservice.service.GeoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geo/country")
@RequiredArgsConstructor
@Logging(level = LoggingLevel.INFO)
public class GeoController {

    private final GeoService geoService;

    @GetMapping
    public List<CountryDto> findAllCountries() {
        return geoService.findAllCountries();
    }

    @GetMapping("/{countryId}/city")
    public List<CityDto> findAllCitiesInCountry(@PathVariable Long countryId) {
        return geoService.findAllCitiesInCountry(countryId);
    }

    @GetMapping("/saveAll")
    public void saveAll() {
        geoService.saveAllCitiesAndCountries();
    }
}
