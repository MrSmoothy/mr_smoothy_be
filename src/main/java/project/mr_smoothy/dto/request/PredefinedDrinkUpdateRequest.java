package project.mr_smoothy.dto.request;

import jakarta.validation.Valid;
import lombok.Data;
import project.mr_smoothy.entity.PredefinedDrink;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PredefinedDrinkUpdateRequest {
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal basePrice; // ราคาพื้นฐาน (ถ้าไม่กำหนดจะคำนวณจากส่วนผสม)
    
    private PredefinedDrink.Category category;
    
    @Valid
    private List<Ingredient> ingredients;
    
    private Boolean active;
    
    private Boolean popular; // สำหรับตั้งค่า popular menu
    
    @Data
    public static class Ingredient {
        private Long fruitId;
        private Integer quantity;
    }
}

