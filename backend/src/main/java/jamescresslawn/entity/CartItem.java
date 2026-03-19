package jamescresslawn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "cart_items",
        // This constraint prevents the same product being added twice to the same
        // user's cart
        // Instead of two rows, we just increase the quantity
        uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "product_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // ------------------------------------------------
    // ManyToOne = many cart items can belong to ONE user
    // @JoinColumn tells Hibernate the foreign key column name in THIS table
    // ------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER = always load the product when loading a cart item
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @UpdateTimestamp // Hibernate updates this every time the row changes
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
