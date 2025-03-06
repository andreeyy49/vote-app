package voteapp.geostorageservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.client.HhClient;
import voteapp.geostorageservice.dto.AreaDto;
import voteapp.geostorageservice.dto.CityDto;
import voteapp.geostorageservice.dto.CountryDto;
import voteapp.geostorageservice.model.City;
import voteapp.geostorageservice.model.Country;
import voteapp.geostorageservice.utils.CityMapper;
import voteapp.geostorageservice.utils.CountryMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Logging
public class GeoService {

    private final HhClient hhClient;
    private final CityService cityService;
    private final CountryService countryService;

    public List<CountryDto> findAllCountries() {
        List<Country> countries = countryService.findAll();
        return countries.stream().map(CountryMapper::countryToCountryDto).toList();
    }

    private List<CountryDto> initAllCountries() {
        List<CountryDto> countries = hhClient.getCountries();

        countries.forEach(country -> {
            List<City> cities = cityService.findByCountryId(country.getId());
            List<CityDto> cityDtos = cities.stream().map(CityMapper::cityToCityDto).toList();
            country.setCities(cityDtos);
        });

        return countries;
    }

    private List<CityDto> initAllCities() {
        List<AreaDto> areas = hhClient.getAllCities();
        List<CityDto> cities = new ArrayList<>();

        areas.forEach(area -> cities.addAll(areaDtoToListCityDto(area)));

        return cities;
    }

    public List<CityDto> findAllCitiesInCountry(Long countryId) {
        return cityService.findByCountryId(countryId).stream().map(CityMapper::cityToCityDto).toList();
    }

    private List<CityDto> areaDtoToListCityDto(AreaDto parentAreaDto) {
        List<CityDto> cityDto = new ArrayList<>();

        recurseGetAreas(parentAreaDto, cityDto, parentAreaDto.getId());

        return cityDto;
    }

    private void recurseGetAreas(AreaDto areaDto, List<CityDto> cityDto, Long countryId) {
        if (areaDto.getAreas().isEmpty()) {
            addToCityList(areaDto, cityDto, countryId);
        } else {
            areaDto.getAreas().forEach(area -> recurseGetAreas(area, cityDto, countryId));
        }
    }

    private void addToCityList(AreaDto area, List<CityDto> cityDto, Long countryId) {
        CityDto city = new CityDto();
        city.setId(area.getId());
        city.setCountryId(countryId);
        city.setTitle(area.getName());

        cityDto.add(city);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Moscow")
    public void saveAllCitiesAndCountries() {
        List<CityDto> citiesDto = initAllCities();
        List<CountryDto> countriesDto = initAllCountries();

        // Группируем города по странам
        Map<Long, List<City>> countryCitiesMap = citiesDto.stream()
                .collect(Collectors.groupingBy(
                        CityDto::getCountryId,
                        Collectors.mapping(dto -> CityMapper.cityDtoToCity(dto, null), Collectors.toList())
                ));

        // Создаем список стран и подготовляем их для пакетного сохранения
        List<Country> countries = countriesDto.stream()
                .map(dto -> {
                    Country country = CountryMapper.countryDtoToCountry(dto);
                    List<City> citiesForCountry = countryCitiesMap.getOrDefault(dto.getId(), new ArrayList<>());
                    citiesForCountry.forEach(city -> city.setCountry(country));
                    country.setCities(citiesForCountry);
                    return country;
                })
                .toList();

        countryService.saveAll(countries);
    }
}
