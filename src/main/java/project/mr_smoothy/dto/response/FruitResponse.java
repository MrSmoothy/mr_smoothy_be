package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.mr_smoothy.entity.Fruit;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FruitResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal pricePerUnit;
    private String imageUrl;
    private Fruit.Category category;
    private Boolean active;
}

