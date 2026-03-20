package jamescresslawn.config;

import jamescresslawn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Separated into its own class to break the circular dependency:
 *
 * BEFORE (circular):
 *   JwtAuthFilter → needs UserDetailsService
 *   SecurityConfig → defines UserDetailsService AND needs JwtAuthFilter
 *   Result: A needs B, B needs A = Spring can't create either
 *
 * AFTER (fixed):
 *   JwtAuthFilter → needs UserDetailsService (from THIS class)
 *   SecurityConfig → needs JwtAuthFilter (no longer defines UserDetailsService)
 *   UserDetailsServiceConfig → defines UserDetailsService (no dependencies on the others)
 *   Result: clean, no cycle
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