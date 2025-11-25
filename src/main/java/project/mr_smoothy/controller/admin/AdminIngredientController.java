package project.mr_smoothy.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.request.IngredientAddRequest;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.IngredientAddResponse;
import project.mr_smoothy.service.IngredientService;

/**
 * Admin Controller for managing ingredients with USDA and OpenAI integration
 */
@RestController
@RequestMapping("/api/admin/ingredient")
@RequiredArgsConstructor
@Slf4j
public class AdminIngredientController {

    private final IngredientService ingredientService;

    /**
     * Add new ingredient with USDA and OpenAI processing
     * POST /api/admin/ingredient
     * 
     * Flow:
     * 1. Receive ingredient name
     * 2. Call USDA API to get nutrition data
     * 3. Send USDA data to OpenAI for processing
     * 4. Save to database
     * 5. Return result
     */
    @PostMapping
    public ResponseEntity<ApiResponse<IngredientAddResponse>> addIngredient(
            @Valid @RequestBody IngredientAddRequest request) {
        log.info("POST /api/admin/ingredient - Adding ingredient: {}", request.getName());
        
        try {
            IngredientAddResponse response = ingredientService.addIngredientWithNutrition(request);
            return ResponseEntity.ok(ApiResponse.success("Ingredient added successfully with nutrition data", response));
        } catch (Exception e) {
            log.error("Error adding ingredient: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to add ingredient: " + e.getMessage()));
        }
    }

    /**
     * Fetch and update nutrition data for existing ingredient
     * POST /api/admin/ingredient/{id}/fetch-nutrition
     * 
     * Flow:
     * 1. Find existing ingredient by ID
     * 2. Call USDA API to get nutrition data
     * 3. Send USDA data to OpenAI for processing
     * 4. Update ingredient with nutrition data
     * 5. Return result
     */
    @PostMapping("/{id}/fetch-nutrition")
    public ResponseEntity<ApiResponse<IngredientAddResponse>> fetchNutrition(@PathVariable Long id) {
        log.info("POST /api/admin/ingredient/{}/fetch-nutrition - Fetching nutrition data", id);
        
        try {
            IngredientAddResponse response = ingredientService.fetchAndUpdateNutrition(id);
            return ResponseEntity.ok(ApiResponse.success("Nutrition data fetched and updated successfully", response));
        } catch (Exception e) {
            log.error("Error fetching nutrition data: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to fetch nutrition data: " + e.getMessage()));
        }
    }
}

