package project.mr_smoothy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.request.CartAddItemRequest;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.CartResponse;
import project.mr_smoothy.service.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        return ResponseEntity.ok(ApiResponse.success("OK", cartService.getMyCart()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody CartAddItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Item added", cartService.addItem(request)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed", cartService.removeItem(itemId)));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<CartResponse>> clear() {
        return ResponseEntity.ok(ApiResponse.success("Cleared", cartService.clearMyCart()));
    }
}


