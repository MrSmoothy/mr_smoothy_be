package project.mr_smoothy.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CupSizeCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Volume in ML is required")
    @Min(value = 1, message = "Volume must be at least 1 ML")
    private Integer volumeMl;
    
    @NotNull(message = "Price extra is required")
    @DecimalMin(value = "0.0", message = "Price extra must be positive")
    private BigDecimal priceExtra;
    
    private Boolean active = true;
}

