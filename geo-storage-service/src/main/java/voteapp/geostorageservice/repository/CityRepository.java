package voteapp.geostorageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import voteapp.geostorageservice.model.City;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    Optional<List<City>> findCitiesByCountryId(Long countryId);
}
