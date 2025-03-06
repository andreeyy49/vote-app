package voteapp.geostorageservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.model.Country;
import voteapp.geostorageservice.repository.CountryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Logging
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    public void saveAll(List<Country> countries) {
        countryRepository.saveAll(countries);
    }

}
