package voteapp.geostorageservice.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.model.Image;
import voteapp.geostorageservice.repository.ImageRepository;
import voteapp.geostorageservice.utils.ImageUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Logging
public class ImageService {

    private final ImageRepository imageRepository;

    private final S3Service s3Service;

    public List<Image> findAll() {
        return imageRepository.findAll();
    }

    public Image findById(String id) {
        return imageRepository.findById(id).orElse(null);
    }

    public Image save(Image image) {
        return imageRepository.save(image);
    }

    public String checkAndSave(Image image, ObjectMetadata metadata) {
        List<Image> images = imageRepository.findByHash(image.getHash());

        boolean metadataEquals;

        if (images.isEmpty()) {
            image.setCount(1);
            save(image);

            log.info("Дубликатов не найдено");
            return null;
        }

        for(Image i : images) {
            ObjectMetadata existMetadata = s3Service.getMetadata(i.getId());
            metadataEquals = ImageUtils.compareMetadata(existMetadata, metadata);
            if (metadataEquals) {
                image.setId(i.getId());
                image.setCount(i.getCount() + 1);
                update(image.getId(), image);
                log.info("Дубликат найден");
                break;
            }
        }

        return image.getId();
    }

    public Image update(String id, Image image) {
        Image existingImage = findById(id);
        existingImage.setHash(image.getHash());
        existingImage.setCount(image.getCount());

        return imageRepository.save(existingImage);
    }

    public void deleteById(String id) {
        imageRepository.deleteById(id);
    }

}
