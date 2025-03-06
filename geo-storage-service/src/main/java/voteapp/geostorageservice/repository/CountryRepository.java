package voteapp.geostorageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import voteapp.geostorageservice.model.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
