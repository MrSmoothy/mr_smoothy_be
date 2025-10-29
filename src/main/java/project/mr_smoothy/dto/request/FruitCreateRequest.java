package project.mr_smoothy.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FruitCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal pricePerUnit;
    
    private String imageUrl;
    
    private Boolean active = true;
}

