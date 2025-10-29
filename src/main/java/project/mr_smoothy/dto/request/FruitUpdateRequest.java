package project.mr_smoothy.dto.request;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FruitUpdateRequest {
    private String name;
    private String description;
    
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal pricePerUnit;
    
    private String imageUrl;
    private Boolean active;
}

