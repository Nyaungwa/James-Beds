package jamescresslawn.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility component for creating, validating, and parsing JWT tokens.
 * Tokens are signed with HMAC-SHA256 using the secret configured in application.properties.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Derives the HMAC-SHA256 signing key from the configured secret string.
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a signed JWT token for the given user.
     * The token subject is set to the user's username (email).
     *
     * @param userDetails the authenticated user
     * @return a compact, signed JWT string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the email address (subject) embedded in a JWT token.
     *
     * @param token the JWT string
     * @return the email stored as the token subject
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates a token by confirming the subject matches the given user
     * and that the token has not expired.
     *
     * @param token       the JWT string
     * @param userDetails the user to validate against
     * @return {@code true} if the token is valid for the given user
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic helper that extracts any single claim from the token payload.
     *
     * @param token          the JWT string
     * @param claimsResolver function mapping {@link Claims} to the desired value
     * @param <T>            the return type
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and verifies the full token using the signing key.
     * Throws a {@code JwtException} if the token is invalid or has been tampered with.
     *
     * @param token the JWT string
     * @return all claims from the verified token body
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
