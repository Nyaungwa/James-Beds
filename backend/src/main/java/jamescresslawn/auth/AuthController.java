package jamescresslawn.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // Allow React frontend to call this
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     *
     * Request body:
     * {
     *   "fullName": "Edwin Nyaungwa",
     *   "email": "edwin@example.com",
     *   "password": "securepassword",
     *   "phoneNumber": "0712345678"   (optional)
     * }
     *
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "email": "edwin@example.com",
     *   "fullName": "Edwin Nyaungwa",
     *   "role": "USER",
     *   "message": "Registration successful"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // Return 400 with the error message (e.g. "Email already registered")
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login
     *
     * Request body:
     * {
     *   "email": "edwin@example.com",
     *   "password": "securepassword"
     * }
     *
     * Response:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiJ9...",
     *   "email": "edwin@example.com",
     *   "fullName": "Edwin Nyaungwa",
     *   "role": "USER",
     *   "message": "Login successful"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return 401 Unauthorized for bad credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
    }

    /**
     * GET /api/auth/me
     * Returns the currently authenticated user's info.
     * Useful for the frontend to check who is logged in.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        // The JWT filter already validated the token before this runs
        // Spring Security has the user in context — we just return a confirmation
        return ResponseEntity.ok(Map.of("message", "Token is valid"));
    }
}