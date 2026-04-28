package jamescresslawn.config;

import jamescresslawn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Defines the {@link UserDetailsService} bean in a dedicated configuration class
 * to break the circular dependency between {@link SecurityConfig} and
 * {@link jamescresslawn.jwt.JwtAuthFilter}: both previously required each other
 * via {@code UserDetailsService}, preventing Spring from constructing either bean.
 */
@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}