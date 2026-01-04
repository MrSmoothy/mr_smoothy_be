package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.mr_smoothy.entity.PredefinedDrink;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredefinedDrinkResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private java.math.BigDecimal basePrice;
    private PredefinedDrink.Category category;
    private Boolean active;
    private Boolean popular;
    private List<IngredientInfo> ingredients;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientInfo {
        private Long fruitId;
        private String fruitName;
        private Integer quantity;
    }
}

