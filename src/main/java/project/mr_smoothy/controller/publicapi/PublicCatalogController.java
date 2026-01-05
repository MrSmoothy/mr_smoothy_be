package project.mr_smoothy.controller.publicapi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.request.GuestOrderCreateRequest;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.CupSizeResponse;
import project.mr_smoothy.dto.response.FruitResponse;
import project.mr_smoothy.dto.response.OrderResponse;
import project.mr_smoothy.dto.response.PredefinedDrinkResponse;
import project.mr_smoothy.service.CupSizeService;
import project.mr_smoothy.service.FruitService;
import project.mr_smoothy.service.OrderService;
import project.mr_smoothy.service.PredefinedDrinkService;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final FruitService fruitService;
    private final CupSizeService cupSizeService;
    private final PredefinedDrinkService predefinedDrinkService;
    private final OrderService orderService;

    @GetMapping("/fruits")
    public ResponseEntity<ApiResponse<List<FruitResponse>>> listFruits() {
        return ResponseEntity.ok(ApiResponse.success("OK", fruitService.listActive()));
    }

    @GetMapping("/cup-sizes")
    public ResponseEntity<ApiResponse<List<CupSizeResponse>>> listCupSizes() {
        return ResponseEntity.ok(ApiResponse.success("OK", cupSizeService.listActive()));
    }

    @GetMapping("/drinks")
    public ResponseEntity<ApiResponse<List<PredefinedDrinkResponse>>> listDrinks(
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(ApiResponse.success("OK", predefinedDrinkService.list(sortBy)));
    }

    @GetMapping("/drinks/popular")
    public ResponseEntity<ApiResponse<List<PredefinedDrinkResponse>>> listPopularDrinks(
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(ApiResponse.success("OK", predefinedDrinkService.listPopular(sortBy)));
    }

    @GetMapping("/seasonal-ingredients")
    public ResponseEntity<ApiResponse<List<FruitResponse>>> listSeasonalIngredients() {
        return ResponseEntity.ok(ApiResponse.success("OK", fruitService.listSeasonal()));
    }

    @PostMapping("/guest-orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createGuestOrder(@Valid @RequestBody GuestOrderCreateRequest request) {
        OrderResponse response = orderService.createGuestOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Guest order created successfully", response));
    }

    @GetMapping("/guest-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getGuestOrdersByPhoneNumber(
            @RequestParam String phoneNumber) {
        List<OrderResponse> orders = orderService.getGuestOrdersByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success("OK", orders));
    }

    @GetMapping("/guest-orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getGuestOrderById(@PathVariable Long orderId) {
        OrderResponse order = orderService.getGuestOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success("OK", order));
    }
}


