package project.mr_smoothy.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.service.MinioService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class AdminImageController {

    private final MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            String url = minioService.uploadFile(file, folder);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("filename", file.getOriginalFilename());
            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", result));
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

    @PostMapping("/upload/fruit")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFruitImage(@RequestParam("file") MultipartFile file) {
        return uploadImage(file, "fruits");
    }

    @PostMapping("/upload/drink")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadDrinkImage(@RequestParam("file") MultipartFile file) {
        return uploadImage(file, "drinks");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteImage(@RequestParam("url") String url) {
        try {
            String objectName = extractObjectName(url);
            minioService.deleteFile(objectName);
            return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", "OK"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image: " + e.getMessage());
        }
    }

    private String extractObjectName(String url) {
        String bucketName = "mr-smoothy-images";
        int bucketIndex = url.indexOf(bucketName);
        if (bucketIndex >= 0) {
            return url.substring(bucketIndex + bucketName.length() + 1);
        }
        throw new RuntimeException("Invalid URL format");
    }
}

