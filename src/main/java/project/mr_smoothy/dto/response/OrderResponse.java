package project.mr_smoothy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private List<OrderItemResponse> items;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime pickupTime;
    private String phoneNumber;
    private String notes;
    private String customerName; // สำหรับ guest orders
    private String customerEmail; // สำหรับ guest orders
    private String username; // สำหรับ logged-in users
    private String userEmail; // สำหรับ logged-in users
    private String userFullName; // สำหรับ logged-in users
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

