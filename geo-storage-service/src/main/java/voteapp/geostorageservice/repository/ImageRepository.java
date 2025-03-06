package voteapp.geostorageservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import voteapp.geostorageservice.model.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, String> {

    List<Image> findByHash(String hash);
}
