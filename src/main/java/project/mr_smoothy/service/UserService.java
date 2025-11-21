package project.mr_smoothy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.mr_smoothy.dto.request.UserCreateRequest;
import project.mr_smoothy.dto.request.UserUpdateRequest;
import project.mr_smoothy.dto.response.UserResponse;
import project.mr_smoothy.entity.User;
import project.mr_smoothy.repository.UserRepository;
import project.mr_smoothy.util.AuthUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToResponse(user);
    }

    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating user: {}", request.getUsername());
        
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
        log.info("User created successfully with id: {}", savedUser.getId());
        
        return convertToResponse(savedUser);
    }

    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return convertToResponse(user);
    }

    /**
     * Gets the current authenticated user's profile.
     * This method follows OOP principles by encapsulating the user retrieval logic.
     * 
     * @return UserResponse containing the current user's data
     * @throws RuntimeException if user is not authenticated
     */
    public UserResponse getCurrentUser() {
        log.info("Fetching current user profile");
        User user = authUtil.getCurrentUser();
        return convertToResponse(user);
    }

    /**
     * Updates the current authenticated user's profile.
     * This method follows OOP principles by encapsulating the update logic.
     * Only provided fields will be updated.
     * 
     * @param request The update request containing new field values
     * @return UserResponse containing the updated user data
     * @throws RuntimeException if user is not authenticated or email already exists
     */
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        log.info("Updating current user profile");
        User user = authUtil.getCurrentUser();
        
        updateUserFields(user, request);
        
        User savedUser = userRepository.save(user);
        log.info("User profile updated successfully for user: {}", savedUser.getUsername());
        
        return convertToResponse(savedUser);
    }

    /**
     * Updates user entity fields based on the update request.
     * This method encapsulates the field update logic following OOP principles.
     * 
     * @param user The user entity to update
     * @param request The update request containing new field values
     */
    private void updateUserFields(User user, UserUpdateRequest request) {
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String email = request.getEmail().trim();
            // Check if email is already taken by another user
            if (userRepository.existsByEmail(email) && !email.equals(user.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(email);
        }
        
        if (request.getPhoneNumber() != null) {
            String phoneNumber = request.getPhoneNumber().trim();
            user.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);
        }
        
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

