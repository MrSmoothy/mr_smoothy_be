package project.mr_smoothy.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CupSizeUpdateRequest {
    private String name;
    
    @Min(value = 1, message = "Volume must be at least 1 ML")
    private Integer volumeMl;
    
    @DecimalMin(value = "0.0", message = "Price extra must be positive")
    private BigDecimal priceExtra;
    
    private Boolean active;
}

