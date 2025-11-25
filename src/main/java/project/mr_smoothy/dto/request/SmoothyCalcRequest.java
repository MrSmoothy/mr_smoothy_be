package project.mr_smoothy.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for calculating smoothy nutrition and flavor
 */
@Data
public class SmoothyCalcRequest {
    
    @NotEmpty(message = "At least one ingredient is required")
    private List<IngredientAmount> ingredients;
    
    @Data
    public static class IngredientAmount {
        @NotNull(message = "Ingredient ID is required")
        private Long ingredientId;
        
        @NotNull(message = "Amount is required")
        private Double amount; // Amount in grams
    }
}

