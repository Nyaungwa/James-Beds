package jamescresslawn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*
 * UserDetails is a Spring Security interface.
 * By implementing it, Spring Security knows how to use this class
 * for authentication — it knows where to find the password,
 * the username, and the user's roles/permissions.
 */
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotBlank
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    @NotBlank
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    // -----------------------------------------------
    // UserDetails interface methods
    // Spring Security calls these to check the user
    // -----------------------------------------------

    /**
     * Returns the user's permissions/roles.
     * We give each user a single role: ROLE_USER or ROLE_ADMIN.
     * Spring Security expects role names to start with "ROLE_".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Spring Security calls this to get the password for verification.
     * We return passwordHash because we stored the hashed version.
     */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * Spring Security uses this as the unique identifier (username).
     * We use email instead of a username field.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * These four methods control account status.
     * Return true for all = account is active with no restrictions.
     * You can add logic here later (e.g. email verification, banning users).
     */
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public enum Role {
        USER, ADMIN
    }
}