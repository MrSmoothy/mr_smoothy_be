package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;


    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setRole(User.UserRole.USER);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());


        String token = jwtTokenUtil.generateToken(savedUser.getUsername());

        UserResponse userResponse = convertToResponse(savedUser);

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .message("Registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("User attempting to login: {}", request.getUsername());

        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            log.error("User not found: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Invalid password for user: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }

        log.info("User logged in successfully: {}", user.getUsername());

        String token = jwtTokenUtil.generateToken(user.getUsername());

        UserResponse userResponse = convertToResponse(user);

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .message("Login successful")
                .build();
    }


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

