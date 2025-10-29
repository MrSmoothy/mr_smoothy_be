package project.mr_smoothy.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class PredefinedDrinkUpdateRequest {
    private String name;
    private String description;
    private String imageUrl;
    
    @Valid
    private List<Ingredient> ingredients;
    
    private Boolean active;
    
    @Data
    public static class Ingredient {
        private Long fruitId;
        private Integer quantity;
    }
}

