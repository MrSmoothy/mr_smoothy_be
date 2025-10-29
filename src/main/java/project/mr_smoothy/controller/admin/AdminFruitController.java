package project.mr_smoothy.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.request.FruitCreateRequest;
import project.mr_smoothy.dto.request.FruitUpdateRequest;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.FruitResponse;
import project.mr_smoothy.service.FruitService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fruits")
@RequiredArgsConstructor
public class AdminFruitController {

    private final FruitService fruitService;

    @PostMapping
    public ResponseEntity<ApiResponse<FruitResponse>> create(@Valid @RequestBody FruitCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Created", fruitService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FruitResponse>> update(@PathVariable Long id, @Valid @RequestBody FruitUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Updated", fruitService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        fruitService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted", "OK"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FruitResponse>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("OK", fruitService.get(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FruitResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.success("OK", fruitService.list()));
    }
}


