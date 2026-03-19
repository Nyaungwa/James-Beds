package jamescresslawn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity                          // Tells Hibernate: "make a database table for this class"
@Table(name = "users")           // The table will be named "users" in PostgreSQL
@Getter                          // Lombok: auto-generates all getters
@Setter                          // Lombok: auto-generates all setters
@NoArgsConstructor               // Lombok: auto-generates empty constructor (JPA requires this)
@AllArgsConstructor              // Lombok: auto-generates constructor with all fields
@Builder                         // Lombok: lets you build objects like User.builder().email("...").build()
public class User {

    @Id                                           // This field is the primary key
    @GeneratedValue(strategy = GenerationType.UUID) // PostgreSQL auto-generates a UUID for each new user
    private String id;

    @Column(nullable = false, unique = true)      // email column: required + must be unique
    @Email                                        // Validates email format
    @NotBlank
    private String email;

    @Column(name = "password_hash", nullable = false) // We never store plain passwords - always hashed
    @NotBlank
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    @NotBlank
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    // A user can have one of two roles: USER or ADMIN
    // @Enumerated stores the text name ("USER", "ADMIN") not a number
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;               // Default role when a user registers is USER

    @CreationTimestamp                           // Hibernate automatically sets this when the record is created
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ------------------------------------------------
    // RELATIONSHIPS
    // One user can have many orders (OneToMany)
    // "mappedBy = user" means the Order entity owns this relationship (has the foreign key)
    // CascadeType.ALL = if you delete a user, their orders are also deleted
    // FetchType.LAZY = don't load orders from DB unless you explicitly ask for them (better performance)
    // ------------------------------------------------
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    // Enum defined inside the User class since it only belongs to User
    public enum Role {
        USER, ADMIN
    }
}

