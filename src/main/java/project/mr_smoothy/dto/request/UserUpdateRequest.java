package project.mr_smoothy.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String password; // Optional - only update if provided
}

