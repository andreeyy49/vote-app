package voteapp.geostorageservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.dto.ImageFileDto;
import voteapp.geostorageservice.dto.ImageStaticsDto;
import voteapp.geostorageservice.exception.BadRequestException;
import voteapp.geostorageservice.model.Image;
import voteapp.geostorageservice.utils.HashUtil;
import voteapp.geostorageservice.utils.ImageParameters;
import voteapp.geostorageservice.utils.ImageUtils;
import voteapp.geostorageservice.utils.StaticWords;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Logging
public class S3StorageService {

    private final AmazonS3 s3Client;

    @Value("${aws.baseUrl}")
    private String baseUrl;

    @Value("${aws.bucket-name}")
    private String bucket;

    @Value("${aws.folder}")
    private String s3Folder;

    private final ImageService imageService;

    /**
     * Проверяет подключение к бакету.
     *
     * @return true, если подключение успешно, иначе false.
     */
    public boolean checkBucketConnection() {
        try {
            // Пытаемся получить список объектов в бакете
            ObjectListing objectListing = s3Client.listObjects(bucket);
            log.info("Подключение к бакету успешно! Количество объектов: {}", objectListing.getObjectSummaries().size());
            return true;
        } catch (AmazonS3Exception e) {
            // Обрабатываем ошибку
            log.error("Ошибка при подключении к бакету: {}", e.getMessage());
            log.error("Код ошибки: {}", e.getErrorCode());
            log.error("Статус код: {}", e.getStatusCode());
            return false;
        } catch (Exception e) {
            // Обрабатываем другие исключения
            log.error("Произошла ошибка: {}", e.getMessage());
            return false;
        }
    }

    public ImageFileDto uploadImage(MultipartFile file) {
        byte[] bytes;
        String base64String;
        try {
            bytes = file.getBytes();
            base64String = Base64.getEncoder().encodeToString(bytes);
        } catch (IllegalArgumentException | IOException e) {
            log.error("{} in uploadImage", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

        String imageId = s3Folder + UUID.randomUUID() + ".jpeg";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType("image/jpeg");

        Map<ImageParameters, Integer> widthAndHeight = ImageUtils.getWidthAndHeightImage(new ByteArrayInputStream(bytes));
        metadata.addUserMetadata(ImageParameters.WIDTH.toString(), widthAndHeight.get(ImageParameters.WIDTH).toString());
        metadata.addUserMetadata(ImageParameters.HEIGHT.toString(), widthAndHeight.get(ImageParameters.HEIGHT).toString());

        try {
            String existImageUrl = saveImageToDb(base64String, imageId, metadata);
            if (existImageUrl != null) {
                log.info("Дубль в s3Service");
                return new ImageFileDto(getFullUrl(existImageUrl));
            }

            PutObjectRequest request = new PutObjectRequest(
                    bucket,
                    imageId,
                    inputStream,
                    metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead); // Устанавливаем ACL здесь

            s3Client.putObject(request);
        } catch (AmazonS3Exception e) {
            log.error("S3client!!! {} in uploadImage", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in uploadImage: {}", e.getMessage());
        }

        return new ImageFileDto(getFullUrl(imageId));
    }

    public void removeImage(String path) {
        String idImage = path.replace(baseUrl + bucket + "/", "");

        if (idImage.contains(StaticWords.REMOVE.toString())) {
            String id = idImage.replace(StaticWords.REMOVE.toString(), "");
            id = id.replace("team-project-52/", "");
            s3Client.deleteObject(bucket, id);
            return;
        }

        Image image = imageService.findById(idImage);

        if (image == null) {
            s3Client.deleteObject(bucket, idImage);
        } else if (image.getCount() == 1) {
            s3Client.deleteObject(bucket, image.getId());
            imageService.deleteById(image.getId());
        } else {
            image.setCount(image.getCount() - 1);
            imageService.update(image.getId(), image);
        }
    }

    public void removeImage(List<String> paths) {
        paths.forEach(this::removeImage);
    }

    public List<ImageStaticsDto> getStorageContent() {
        List<ImageStaticsDto> storageContent = new ArrayList<>();

        String continuationToken = null;

        do {
            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName("social-network-storage")
                    .withPrefix("team-project-52/")
                    .withContinuationToken(continuationToken);

            ListObjectsV2Result result = s3Client.listObjectsV2(request);

            for (S3ObjectSummary object : result.getObjectSummaries()) {
                if (object.getKey().endsWith("/")) {
                    continue;
                }
                storageContent.add(
                        new ImageStaticsDto(
                                baseUrl + "social-network-storage/" + object.getKey(),
                                object.getSize(),
                                object.getLastModified().toString()
                        )
                );

                List<Image> images = imageService.findAll();
                for(ImageStaticsDto dto : storageContent) {
                    for(Image image : images) {
                        if(dto.getUrl().contains(image.getId())) {
                            dto.setCount(image.getCount());
                            break;
                        }
                    }
                }
            }

            continuationToken = result.getNextContinuationToken();

        } while (continuationToken != null);

        return storageContent;
    }

    public List<ImageStaticsDto> removeNoUsageImage() {
        List<ImageStaticsDto> storageContent = getStorageContent();
        List<ImageStaticsDto> result = new ArrayList<>();

        storageContent.forEach(el -> {
            el.setUrl(ImageUtils.getImageIdForDb(el.getUrl()));
            if (el.getUrl().contains(StaticWords.REMOVE.toString())) {
                removeImage(getFullUrl(el.getUrl()));
                return;
            }

            if (imageService.findById(el.getUrl()) == null) {
                removeImage(getFullUrl(el.getUrl()));
                return;
            }

            result.add(el);
        });

        return result;
    }

    private String saveImageToDb(String file, String imageId, ObjectMetadata objectMetadata) {
        Image image = new Image();
        image.setId(imageId);
        image.setHash(HashUtil.hash(file));

        return imageService.checkAndSave(image, objectMetadata);
    }

    private String getFullUrl(String imageId) {
        return baseUrl + bucket + "/" + imageId;
    }
}