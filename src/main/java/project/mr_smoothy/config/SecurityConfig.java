package project.mr_smoothy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class SecurityConfig {

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:3000,https://minio.thanonchai.site,https://mr-smoothy.thanonchai.site,https://mr-smoothy-api.thetigerteamacademy.net}")
    private String corsAllowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Parse allowed origins from environment variable (comma-separated)
                String[] origins = parseAllowedOrigins();
                
                log.info("CORS Configuration - Allowed Origins: {}", String.join(", ", origins));
                
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }

    private String[] parseAllowedOrigins() {
        if (!StringUtils.hasText(corsAllowedOrigins)) {
            // Default origins if not configured
            return new String[]{
                "http://localhost:3000",
                "https://mr-smoothy.thetigerteamacademy.net"
            };
        }
        
        // Split by comma and trim whitespace from each origin
        return java.util.Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
    }
}

