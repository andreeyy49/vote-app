package voteapp.geostorageservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import voteapp.geostorageservice.aop.Logging;

@Service
@RequiredArgsConstructor
@Logging
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.baseUrl}")
    private String baseUrl;

    @Value("${aws.bucket-name}")
    private String bucket;

    public ObjectMetadata getMetadata(String url) {
        return s3Client.getObjectMetadata(bucket, url);
    }
}
