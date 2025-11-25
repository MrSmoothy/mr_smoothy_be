package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.mr_smoothy.entity.Fruit;

import java.math.BigDecimal;

/**
 * Response DTO for ingredient addition with nutrition data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientAddResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal pricePerUnit;
    private Fruit.Category category;
    private Boolean active;
    private Boolean seasonal;
    
    // Nutrition Data
    private BigDecimal calorie;
    private BigDecimal protein;
    private BigDecimal fiber;
    private String vitamins; // JSON string
    private String minerals; // JSON string
    
    // Flavor & Pairing Data
    private String flavorProfile;
    private String tasteNotes;
    private String bestMixPairing; // JSON string
    private String avoidPairing; // JSON string
}

