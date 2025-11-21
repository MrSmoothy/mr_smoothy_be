package project.mr_smoothy.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequest {
    private String fullName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String phoneNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBirth;
    
    private String password; // Optional - only update if provided
}

