package project.mr_smoothy.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GuestOrderCreateRequest {

    @NotNull(message = "Pickup time is required")
    @Future(message = "Pickup time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime pickupTime;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    private String customerEmail;

    private String notes;

    @NotNull(message = "Items are required")
    private List<GuestOrderItemRequest> items;

    @Data
    public static class GuestOrderItemRequest {
        @NotNull(message = "Type is required")
        private String type; // "PREDEFINED" or "CUSTOM"

        @NotNull(message = "Cup size ID is required")
        private Long cupSizeId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private Long predefinedDrinkId; // สำหรับ PREDEFINED type

        private List<GuestOrderItemFruitRequest> fruits; // สำหรับ CUSTOM type

        @NotNull(message = "Unit price is required")
        private BigDecimal unitPrice;

        @NotNull(message = "Total price is required")
        private BigDecimal totalPrice;
    }

    @Data
    public static class GuestOrderItemFruitRequest {
        @NotNull(message = "Fruit ID is required")
        private Long fruitId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;
    }
}

