package project.mr_smoothy.controller.publicapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.CupSizeResponse;
import project.mr_smoothy.dto.response.FruitResponse;
import project.mr_smoothy.dto.response.PredefinedDrinkResponse;
import project.mr_smoothy.service.CupSizeService;
import project.mr_smoothy.service.FruitService;
import project.mr_smoothy.service.PredefinedDrinkService;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final FruitService fruitService;
    private final CupSizeService cupSizeService;
    private final PredefinedDrinkService predefinedDrinkService;

    @GetMapping("/fruits")
    public ResponseEntity<ApiResponse<List<FruitResponse>>> listFruits() {
        return ResponseEntity.ok(ApiResponse.success("OK", fruitService.listActive()));
    }

    @GetMapping("/cup-sizes")
    public ResponseEntity<ApiResponse<List<CupSizeResponse>>> listCupSizes() {
        return ResponseEntity.ok(ApiResponse.success("OK", cupSizeService.listActive()));
    }

    @GetMapping("/drinks")
    public ResponseEntity<ApiResponse<List<PredefinedDrinkResponse>>> listDrinks() {
        return ResponseEntity.ok(ApiResponse.success("OK", predefinedDrinkService.list()));
    }

    @GetMapping("/seasonal-ingredients")
    public ResponseEntity<ApiResponse<List<FruitResponse>>> listSeasonalIngredients() {
        return ResponseEntity.ok(ApiResponse.success("OK", fruitService.listSeasonal()));
    }
}


