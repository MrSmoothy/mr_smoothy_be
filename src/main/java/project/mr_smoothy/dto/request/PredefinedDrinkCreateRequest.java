package project.mr_smoothy.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import project.mr_smoothy.entity.PredefinedDrink;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PredefinedDrinkCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String imageUrl;
    private BigDecimal basePrice; // ราคาพื้นฐาน (ถ้าไม่กำหนดจะคำนวณจากส่วนผสม)
    
    private PredefinedDrink.Category category;
    
    @NotEmpty(message = "At least one ingredient is required")
    @Valid
    private List<Ingredient> ingredients;
    
    private Boolean active = true;
    
    private Boolean popular = false; // สำหรับแสดงในหน้า home popular menu
    
    @Data
    public static class Ingredient {
        @NotNull(message = "Fruit ID is required")
        private Long fruitId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}

