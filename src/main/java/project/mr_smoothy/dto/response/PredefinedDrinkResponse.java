package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Boolean active;
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

