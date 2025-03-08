package voteapp.geostorageservice.utils;

import lombok.experimental.UtilityClass;
import voteapp.geostorageservice.dto.CityDto;
import voteapp.geostorageservice.dto.CountryDto;
import voteapp.geostorageservice.model.City;
import voteapp.geostorageservice.model.Country;

@UtilityClass
public class CountryMapper {

    public static Country countryDtoToCountry(CountryDto countryDto) {
        Country country = new Country();
        country.setId(countryDto.getId());
        country.setTitle(countryDto.getTitle());
        if (!countryDto.getCities().isEmpty()) {
            country.setCities(countryDto.getCities().stream().map(dto -> {
                City city = new City();
                city.setId(dto.getId());
                city.setTitle(dto.getTitle());
                return city;
            }).toList());
        }

        return country;
    }

    public static CountryDto countryToCountryDto(Country country) {
        CountryDto countryDto = countryToCountryDtoWithoutCities(country);
        if (!country.getCities().isEmpty()) {
            countryDto.setCities(country.getCities().stream().map(dto -> {
                CityDto cityDto = new CityDto();
                cityDto.setId(dto.getId());
                cityDto.setTitle(dto.getTitle());
                cityDto.setCountryId(countryDto.getId());
                return cityDto;
            }).toList());
        }

        return countryDto;
    }

    public static CountryDto countryToCountryDtoWithoutCities(Country country) {
        CountryDto countryDto = new CountryDto();
        countryDto.setId(country.getId());
        countryDto.setTitle(country.getTitle());

        return countryDto;
    }
}
