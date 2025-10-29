package project.mr_smoothy.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.request.CupSizeCreateRequest;
import project.mr_smoothy.dto.request.CupSizeUpdateRequest;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.CupSizeResponse;
import project.mr_smoothy.service.CupSizeService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cup-sizes")
@RequiredArgsConstructor
public class AdminCupSizeController {

    private final CupSizeService cupSizeService;

    @PostMapping
    public ResponseEntity<ApiResponse<CupSizeResponse>> create(@Valid @RequestBody CupSizeCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Created", cupSizeService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CupSizeResponse>> update(@PathVariable Long id, @Valid @RequestBody CupSizeUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated", cupSizeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        cupSizeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted", "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CupSizeResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("OK", cupSizeService.get(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CupSizeResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success("OK", cupSizeService.list()));
    }
}


