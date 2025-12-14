package project.mr_smoothy.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for adding ingredient with USDA and OpenAI processing
 */
@Data
public class IngredientAddRequest {
    
    @NotBlank(message = "Ingredient name is required")
    private String name;
    
    // Optional fields - can be set manually or left for USDA/OpenAI to fill
    private String description;
    private String imageUrl;
    private java.math.BigDecimal pricePerUnit;
    private project.mr_smoothy.entity.Fruit.Category category;
    private Boolean active = true;
    private Boolean seasonal = false;
    
    // Option to fetch nutrition data from USDA (default: true for backward compatibility)
    private Boolean fetchNutrition = true;
}

