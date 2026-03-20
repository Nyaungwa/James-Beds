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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * REGISTER
     *
     * Steps:
     * 1. Check if email is already taken
     * 2. Hash the password using BCrypt
     * 3. Save the new user to the database
     * 4. Generate a JWT token for them
     * 5. Return the token (so they're logged in immediately after registering)
     */
    @SuppressWarnings("null")
    public AuthResponse register(RegisterRequest request) {

        // Step 1: Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        // Step 2 & 3: Hash the password and save the user
        // passwordEncoder.encode() converts "mypassword" → "$2a$10$xyz..."
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))  // NEVER store plain text
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        // Step 4: Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Step 5: Return the response
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Registration successful")
                .build();
    }

    /**
     * LOGIN
     *
     * Steps:
     * 1. Use Spring's AuthenticationManager to verify email + password
     *    - It loads the user from DB using UserDetailsService
     *    - It compares the provided password against the stored BCrypt hash
     *    - If wrong password: throws BadCredentialsException automatically
     * 2. Load the user from DB
     * 3. Generate a JWT token
     * 4. Return the token
     */
    public AuthResponse login(AuthRequest request) {

        // Step 1: Authenticate — this throws an exception if credentials are wrong
        // We don't need to manually check the password — Spring does it for us
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 2: If we reach here, authentication succeeded — load the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in: {}", user.getEmail());

        // Step 3: Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Step 4: Return response with token
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }
}