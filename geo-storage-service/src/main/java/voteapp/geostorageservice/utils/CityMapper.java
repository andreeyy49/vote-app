package voteapp.geostorageservice.utils;

import lombok.experimental.UtilityClass;
import voteapp.geostorageservice.dto.CityDto;
import voteapp.geostorageservice.model.City;
import voteapp.geostorageservice.model.Country;

@UtilityClass
public class CityMapper {

    public static City cityDtoToCity(CityDto dto, Country country) {
        City city = new City();
        city.setId(dto.getId());
        city.setTitle(dto.getTitle());
        city.setCountry(country);
        return city;
    }

    public static CityDto cityToCityDto(City city) {
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setTitle(city.getTitle());
        dto.setCountryId(city.getCountry().getId());
        return dto;
    }
}
