package project.mr_smoothy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import project.mr_smoothy.interceptor.AdminAuthInterceptor;
import project.mr_smoothy.interceptor.UserAuthInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final UserAuthInterceptor userAuthInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Admin endpoints - require ADMIN role
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/auth/**");
        
        // User endpoints - require any authenticated user
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/api/cart/**", "/api/orders/**", "/api/users/**")
                .excludePathPatterns("/api/auth/**", "/api/public/**");
    }
}

