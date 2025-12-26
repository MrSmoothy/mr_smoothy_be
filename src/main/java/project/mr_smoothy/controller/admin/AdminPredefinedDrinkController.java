package project.mr_smoothy.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AdminPredefinedDrinkController {

    private final PredefinedDrinkService predefinedDrinkService;

    @PostMapping
    public ResponseEntity<ApiResponse<PredefinedDrinkResponse>> create(@Valid @RequestBody PredefinedDrinkCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Created", predefinedDrinkService.create(request)));
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
}


