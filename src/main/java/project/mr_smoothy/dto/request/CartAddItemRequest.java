package project.mr_smoothy.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CartAddItemRequest {
    @NotNull(message = "Type is required")
    private Type type;
    
    @NotNull(message = "Cup size ID is required")
    private Long cupSizeId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private Long predefinedDrinkId;
    
    @Valid
    private List<Ingredient> ingredients;
    
    public enum Type {
        PREDEFINED, CUSTOM
    }
    
    @Data
    public static class Ingredient {
        @NotNull(message = "Fruit ID is required")
        private Long fruitId;
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}

