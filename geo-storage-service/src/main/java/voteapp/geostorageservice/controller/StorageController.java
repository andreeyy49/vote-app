package voteapp.geostorageservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import voteapp.geostorageservice.aop.Logging;
import voteapp.geostorageservice.dto.ImageFileDto;
import voteapp.geostorageservice.service.S3StorageService;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Logging
public class StorageController {

    private final S3StorageService s3StorageService;

    @PostMapping("/storageUserImage")
    public ImageFileDto upload(@RequestParam("file") MultipartFile file) {
        return s3StorageService.uploadImage(file);
    }

    @GetMapping("/deleteByLink")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@RequestParam("linkToDelete") String linkToDelete) {
        s3StorageService.removeImage(linkToDelete);
    }

    @GetMapping
    public boolean checkConnection() {
        return s3StorageService.checkBucketConnection();
    }
}
