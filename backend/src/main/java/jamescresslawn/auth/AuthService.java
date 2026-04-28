package jamescresslawn.auth;

import jamescresslawn.entity.User;
import jamescresslawn.jwt.JwtUtil;
import jamescresslawn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service layer for authentication business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user account and immediately returns a JWT token on success.
     * The password is BCrypt-hashed before storage.
     * Throws a {@code RuntimeException} if the email address is already in use.
     *
     * @param request the registration payload
     * @return an {@link AuthResponse} containing the JWT token and user details
     */
    @SuppressWarnings("null")
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Registration successful")
                .build();
    }

    /**
     * Authenticates a user with email and password and returns a JWT token.
     * Credential verification is delegated to Spring's {@code AuthenticationManager},
     * which throws {@code BadCredentialsException} automatically on failure.
     *
     * @param request the login payload
     * @return an {@link AuthResponse} containing the JWT token and user details
     */
    public AuthResponse login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in: {}", user.getEmail());

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }
}
