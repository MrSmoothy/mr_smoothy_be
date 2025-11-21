package project.mr_smoothy.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkSeasonalUpdateRequest {
    private List<Long> ingredientIds;
    private Boolean seasonal;
}

