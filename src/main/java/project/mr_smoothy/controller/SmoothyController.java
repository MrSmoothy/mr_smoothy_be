package project.mr_smoothy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.request.SmoothyCalcRequest;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.SmoothyCalcResponse;
import project.mr_smoothy.service.SmoothyService;

/**
 * Controller for smoothy customization and nutrition calculation
 */
@RestController
@RequestMapping("/api/smoothy")
@RequiredArgsConstructor
@Slf4j
public class SmoothyController {

    private final SmoothyService smoothyService;

    /**
     * Calculate smoothy nutrition and flavor analysis
     * POST /api/smoothy/calc
     * 
     * Flow:
     * 1. Receive ingredient IDs and amounts
     * 2. Fetch ingredient nutrition data from DB
     * 3. Calculate total nutrition
     * 4. Send to OpenAI for flavor analysis
     * 5. Return result
     */
    @PostMapping("/calc")
    public ResponseEntity<ApiResponse<SmoothyCalcResponse>> calculateSmoothy(
            @Valid @RequestBody SmoothyCalcRequest request) {
        log.info("POST /api/smoothy/calc - Calculating smoothy with {} ingredients", 
                request.getIngredients().size());
        
        try {
            SmoothyCalcResponse response = smoothyService.calculateSmoothy(request);
            return ResponseEntity.ok(ApiResponse.success("Smoothy calculated successfully", response));
        } catch (Exception e) {
            log.error("Error calculating smoothy: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to calculate smoothy: " + e.getMessage()));
        }
    }
}

