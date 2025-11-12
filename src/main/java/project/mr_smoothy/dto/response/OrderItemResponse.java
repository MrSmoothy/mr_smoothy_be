package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private String type;
    private String cupSizeName;
    private Integer quantity;
    private String predefinedDrinkName;
    private List<FruitEntry> fruits;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FruitEntry {
        private Long fruitId;
        private String fruitName;
        private Integer quantity;
        private BigDecimal pricePerUnit;
    }
}

