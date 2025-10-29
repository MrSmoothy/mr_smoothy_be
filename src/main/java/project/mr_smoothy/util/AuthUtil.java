package project.mr_smoothy.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import project.mr_smoothy.entity.User;
import project.mr_smoothy.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AuthUtil {
    
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    
    public User getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = extractTokenFromRequest(request);
        
        if (token == null) {
            throw new RuntimeException("Authentication required. Please login.");
        }
        
        try {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}

