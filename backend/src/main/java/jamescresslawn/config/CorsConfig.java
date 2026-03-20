package jamescresslawn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS = Cross-Origin Resource Sharing
 *
 * By default, browsers block requests from one domain to another.
 * Your React frontend runs on http://localhost:5173
 * Your backend runs on http://localhost:8080
 * These are different origins, so the browser blocks it.
 *
 * This config tells the browser: "it's okay, the backend allows requests from the frontend"
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow your React frontend origin
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",  // Vite dev server
                "http://localhost:3000"   // Create React App dev server
        ));

        // Allow these HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow these headers (Authorization is required for JWT)
        config.setAllowedHeaders(List.of("*"));

        // Allow the browser to read the Authorization header in responses
        config.setExposedHeaders(List.of("Authorization"));

        // Allow cookies and Authorization headers
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Apply to all routes

        return new CorsFilter(source);
    }
}