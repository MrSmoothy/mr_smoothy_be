package project.mr_smoothy.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.mr_smoothy.dto.response.ApiResponse;
import project.mr_smoothy.dto.response.OrderResponse;
import project.mr_smoothy.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        log.info("GET /api/admin/orders - Fetching all orders");
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId) {
        log.info("GET /api/admin/orders/{} - Fetching order by id", orderId);
        OrderResponse order = orderService.getOrderByIdForAdmin(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        log.info("PUT /api/admin/orders/{}/status - Updating order status to {}", orderId, request.status);
        OrderResponse order = orderService.updateOrderStatus(orderId, request.status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    // Inner class for request body
    @lombok.Data
    static class UpdateStatusRequest {
        private String status;
    }
}

