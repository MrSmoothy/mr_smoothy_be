package project.mr_smoothy.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.request.PredefinedDrinkCreateRequest;
import project.mr_smoothy.dto.request.PredefinedDrinkUpdateRequest;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.PredefinedDrinkResponse;
import project.mr_smoothy.service.PredefinedDrinkService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/drinks")
@RequiredArgsConstructor
@Slf4j
public class AdminPredefinedDrinkController {

    private final PredefinedDrinkService predefinedDrinkService;

    @PostMapping
    public ResponseEntity<ApiResponse<PredefinedDrinkResponse>> create(@Valid @RequestBody PredefinedDrinkCreateRequest request) {
        log.info("POST /api/admin/drinks - Creating new drink: {}", request.getName());
        try {
            PredefinedDrinkResponse response = predefinedDrinkService.create(request);
            log.info("Successfully created drink with ID: {}", response.getId());
            return ResponseEntity.ok(ApiResponse.success("Created", response));
        } catch (Exception e) {
            log.error("Error creating drink: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PredefinedDrinkResponse>> update(@PathVariable Long id, @Valid @RequestBody PredefinedDrinkUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated", predefinedDrinkService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        predefinedDrinkService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted", "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PredefinedDrinkResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("OK", predefinedDrinkService.get(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PredefinedDrinkResponse>>> list() {
        // สำหรับ admin ให้แสดงทุกเมนู รวมทั้งที่ปิดการใช้งาน
        return ResponseEntity.ok(ApiResponse.success("OK", predefinedDrinkService.listAll()));
    }

    @PutMapping("/{id}/popular")
    public ResponseEntity<ApiResponse<PredefinedDrinkResponse>> setPopular(
            @PathVariable Long id,
            @RequestParam Boolean popular) {
        log.info("PUT /api/admin/drinks/{}/popular - Setting popular to: {}", id, popular);
        try {
            PredefinedDrinkResponse response = predefinedDrinkService.setPopular(id, popular);
            log.info("Successfully updated popular status for drink ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(
                    popular ? "Menu set as popular" : "Menu removed from popular",
                    response));
        } catch (Exception e) {
            log.error("Error updating popular status: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
}


