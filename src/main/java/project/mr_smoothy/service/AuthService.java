package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.LoginRequest;
import project.mr_smoothy.dto.request.RegisterRequest;
import project.mr_smoothy.dto.response.AuthResponse;
import project.mr_smoothy.dto.response.UserResponse;
import project.mr_smoothy.entity.User;
import project.mr_smoothy.repository.UserRepository;
import project.mr_smoothy.util.JwtTokenUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * ลงทะเบียน User ใหม่
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // ตรวจสอบ username ซ้ำ
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // ตรวจสอบ email ซ้ำ
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // สร้าง User ใหม่
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(User.UserRole.USER);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // สร้าง Token
        String token = jwtTokenUtil.generateToken(savedUser.getUsername());

        // แปลงเป็น Response
        UserResponse userResponse = convertToResponse(savedUser);

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .message("Registration successful")
                .build();
    }

    /**
     * Login
     */
    public AuthResponse login(LoginRequest request) {
        log.info("User attempting to login: {}", request.getUsername());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ดึงข้อมูล User
            Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOptional.get();
            log.info("User logged in successfully: {}", user.getUsername());

            // สร้าง Token
            String token = jwtTokenUtil.generateToken(user.getUsername());

            // แปลงเป็น Response
            UserResponse userResponse = convertToResponse(user);

            return AuthResponse.builder()
                    .token(token)
                    .user(userResponse)
                    .message("Login successful")
                    .build();

        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            throw new RuntimeException("Invalid username or password");
        }
    }

    /**
     * แปลง User Entity เป็น UserResponse
     */
    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

