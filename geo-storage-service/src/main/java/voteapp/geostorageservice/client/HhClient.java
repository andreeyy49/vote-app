package voteapp.geostorageservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import voteapp.geostorageservice.config.FeignConfig;
import voteapp.geostorageservice.dto.AreaDto;
import voteapp.geostorageservice.dto.CountryDto;

import java.util.List;

@FeignClient(name = "hhClient", url = "${external-api.hhUrl}", configuration = FeignConfig.class)
public interface HhClient {

    @GetMapping(value = "/countries")
    List<CountryDto> getCountries();

    @GetMapping(value = "/{countryId}")
    AreaDto getCities(@PathVariable("countryId") Long countryId);

    @GetMapping
    List<AreaDto> getAllCities();
}