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
 * JwtUtil handles everything related to JWT tokens.
 *
 * What is a JWT?
 * A JWT (JSON Web Token) is a string with 3 parts separated by dots:
 *   header.payload.signature
 *
 * Example:
 *   eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsImlhdCI6MTY...
 *
 * - Header: says which algorithm was used (HS256)
 * - Payload: contains the data (email, expiry time, etc.)
 * - Signature: proves the token wasn't tampered with
 *
 * The server signs the token with a secret key.
 * When the client sends the token back, the server verifies the signature.
 * If the signature is valid, the server trusts the data inside.
 */
@Component
public class JwtUtil {

    // Read the secret key from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Read the expiration time from application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Converts the secret string into a proper cryptographic key.
     * We use HMAC-SHA256 (HS256) algorithm.
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * GENERATE TOKEN
     * Creates a new JWT token for a user after successful login.
     *
     * The token contains:
     * - subject: the user's email (used to identify who the token belongs to)
     * - issuedAt: when the token was created
     * - expiration: when the token expires (24 hours from now)
     * - signature: proves the token is valid
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // You can add extra data to the token here if needed
        // e.g. claims.put("role", user.getRole());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)                                        // email
                .setIssuedAt(new Date(System.currentTimeMillis()))          // now
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // now + 24h
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)         // sign it
                .compact();                                                  // build the string
    }

    /**
     * EXTRACT EMAIL FROM TOKEN
     * Reads the subject (email) we stored inside the token.
     * Called when a request comes in with a token — we need to know WHOSE token it is.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * VALIDATE TOKEN
     * Checks two things:
     * 1. The email in the token matches the user we loaded from the database
     * 2. The token hasn't expired yet
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * CHECK IF TOKEN IS EXPIRED
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * EXTRACT ANY CLAIM FROM TOKEN
     * A "claim" is any piece of data stored inside the token.
     * This is a generic helper used by the specific extract methods above.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * PARSE AND VERIFY THE TOKEN
     * This uses our secret key to verify the token's signature.
     * If the token was tampered with or uses a different key, this throws an exception.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}