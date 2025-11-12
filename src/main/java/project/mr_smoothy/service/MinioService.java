package project.mr_smoothy.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${minio.publicEndpoint:http://localhost:9000}")
    private String publicEndpoint;

    @Value("${minio.bucketName:mr-smoothy-images}")
    private String bucketName;
    
    @PostConstruct
    public void init() {
        log.info("MinIO Configuration - Internal endpoint: {}, Public endpoint: {}, Bucket: {}", 
                endpoint, publicEndpoint, bucketName);
    }
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket {} created", bucketName);
        }
    }

    public String uploadFile(MultipartFile file, String folder) throws Exception {
        ensureBucketExists();

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String filename = UUID.randomUUID().toString() + extension;
        String objectName = folder != null && !folder.isEmpty() ? folder + "/" + filename : filename;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        String url = getFileUrl(objectName);
        log.info("File uploaded: {}", url);
        return url;
    }

    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
        log.info("File deleted: {}", objectName);
    }

    public String getFileUrl(String objectName) {
        // MinIO public URL format: http://public-endpoint/bucket-name/object-path
        // Use publicEndpoint for frontend access (localhost:9000), not internal endpoint (minio:9000)
        // Remove leading slash if exists
        String cleanObjectName = objectName.startsWith("/") ? objectName.substring(1) : objectName;
        String url = publicEndpoint + "/" + bucketName + "/" + cleanObjectName;
        log.info("Generated MinIO public URL: {} (using publicEndpoint: {}, internal endpoint: {})", 
                url, publicEndpoint, endpoint);
        return url;
    }

    public InputStream downloadFile(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }
}

