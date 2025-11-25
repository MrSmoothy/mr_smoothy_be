package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for smoothy calculation result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmoothyCalcResponse {
    
    private TotalNutrition totalNutrition;
    private String flavorDescription;
    private List<String> synergy;
    private List<String> cancellation;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalNutrition {
        private BigDecimal totalCalorie;
        private BigDecimal totalProtein;
        private BigDecimal totalFiber;
    }
}

