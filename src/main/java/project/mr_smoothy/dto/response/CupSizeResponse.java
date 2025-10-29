package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CupSizeResponse {
    private Long id;
    private String name;
    private Integer volumeMl;
    private BigDecimal priceExtra;
    private Boolean active;
}

