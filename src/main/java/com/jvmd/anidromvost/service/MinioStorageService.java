package com.jvmd.anidromvost.service;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.videos}")
    private String videoBucket;

    @Value("${minio.bucket.images}")
    private String imageBucket;

    public MinioStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void initBuckets() {
        createBucketIfNotExists(videoBucket);
        createBucketIfNotExists(imageBucket);
    }

    private void createBucketIfNotExists(String bucket) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created bucket: {}", bucket);
            }
        } catch (Exception e) {
            log.warn("Could not create bucket {}: {}", bucket, e.getMessage());
        }
    }

    public String uploadVideo(MultipartFile file) throws Exception {
        String key = "videos/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(videoBucket)
                .object(key)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        return key;
    }

    public String uploadImage(MultipartFile file) throws Exception {
        String key = "covers/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(imageBucket)
                .object(key)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        return key;
    }

    public InputStream getVideoStream(String objectKey) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(videoBucket)
                .object(objectKey)
                .build());
    }

    public InputStream getVideoStream(String objectKey, long offset, long length) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(videoBucket)
                .object(objectKey)
                .offset(offset)
                .length(length)
                .build());
    }

    public StatObjectResponse getVideoStat(String objectKey) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(videoBucket)
                .object(objectKey)
                .build());
    }

    public InputStream getImageStream(String objectKey) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(imageBucket)
                .object(objectKey)
                .build());
    }

    public StatObjectResponse getImageStat(String objectKey) throws Exception {
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(imageBucket)
                .object(objectKey)
                .build());
    }

    public void deleteVideo(String objectKey) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(videoBucket)
                .object(objectKey)
                .build());
    }

    public void deleteImage(String objectKey) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(imageBucket)
                .object(objectKey)
                .build());
    }
}
