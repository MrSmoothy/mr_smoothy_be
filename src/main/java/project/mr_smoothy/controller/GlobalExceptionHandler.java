package project.mr_smoothy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.mr_smoothy.dto.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        // ซ่อน technical error messages จาก user แต่ log เต็มรูปแบบ
        String userMessage = ex.getMessage();
        if (userMessage != null) {
            // กรอง technical errors
            if (userMessage.contains("allowCredentials") || 
                userMessage.contains("allowedOrigins") ||
                userMessage.contains("CORS") ||
                userMessage.contains("Access-Control")) {
                userMessage = "เกิดข้อผิดพลาดในการเชื่อมต่อ กรุณาลองใหม่อีกครั้ง";
            } else if (userMessage.contains("JSON") || 
                       userMessage.contains("parse") ||
                       userMessage.contains("deserialize") ||
                       userMessage.contains("Unrecognized field")) {
                log.error("JSON parsing error - check request format");
                userMessage = "รูปแบบข้อมูลไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง";
            }
        } else {
            userMessage = "เกิดข้อผิดพลาด กรุณาลองใหม่อีกครั้ง";
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(userMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        log.error("Validation exception occurred");
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .timestamp(java.time.LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoResourceFoundException(
            org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("API endpoint not found: " + ex.getResourcePath()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        // ซ่อน technical error messages จาก user
        String message = ex.getMessage();
        if (message != null) {
            // กรอง technical errors
            if (message.contains("allowCredentials") || 
                message.contains("allowedOrigins") ||
                message.contains("CORS") ||
                message.contains("Access-Control")) {
                message = "เกิดข้อผิดพลาดในการเชื่อมต่อ กรุณาลองใหม่อีกครั้ง";
            } else if (message.contains("IllegalStateException") ||
                       message.contains("ServletException")) {
                message = "เกิดข้อผิดพลาดในการประมวลผล กรุณาลองใหม่อีกครั้ง";
            }
        }
        
        if (message == null || message.isEmpty()) {
            message = "เกิดข้อผิดพลาด กรุณาลองใหม่อีกครั้ง";
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message));
    }
}

