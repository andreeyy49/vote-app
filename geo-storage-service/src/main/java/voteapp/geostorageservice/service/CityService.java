package voteapp.geostorageservice.service;

import com.amazonaws.services.glue.model.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.model.City;
import voteapp.geostorageservice.repository.CityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Logging
public class CityService {
    private final CityRepository cityRepository;

    public List<City> findByCountryId(Long countryId) {
        return cityRepository.findCitiesByCountryId(countryId).orElseThrow(() -> new EntityNotFoundException("country not found"));
    }
}
