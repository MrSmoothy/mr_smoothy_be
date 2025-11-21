package project.mr_smoothy.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderCreateRequest {

    @NotNull(message = "Pickup time is required")
    @Future(message = "Pickup time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime pickupTime;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String notes;
}

