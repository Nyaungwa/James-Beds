package jamescresslawn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    // Full description of the product
    @Column(columnDefinition = "TEXT")   // TEXT allows long descriptions (no character limit)
    private String description;

    // Type of product: BED or MATTRESS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    // Size: SINGLE, THREE_QUARTER, DOUBLE, QUEEN, KING
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductSize size;

    // Comfort level for mattresses: SOFT, MEDIUM, FIRM, EXTRA_FIRM
    @Enumerated(EnumType.STRING)
    @Column(name = "comfort_level")
    private ComfortLevel comfortLevel;

    // ALWAYS use BigDecimal for money - never double or float
    // double causes rounding errors: 99.99 + 0.01 might = 100.00000000001
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @Positive
    private BigDecimal price;

    // Optional discounted price (null = no discount)
    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    // URL to the product image (stored in cloud storage or your server)
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "in_stock", nullable = false)
    @Builder.Default
    private Boolean inStock = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ------------------------------------------------
    // Helper method: returns the effective price
    // If there's a discount price, use that; otherwise use regular price
    // ------------------------------------------------
    public BigDecimal getEffectivePrice() {
        return discountPrice != null ? discountPrice : price;
    }

    // ------------------------------------------------
    // Enums for product properties
    // ------------------------------------------------
    public enum ProductType {
        BED, MATTRESS
    }

    public enum ProductSize {
        SINGLE, THREE_QUARTER, DOUBLE, QUEEN, KING
    }

    public enum ComfortLevel {
        SOFT, MEDIUM, FIRM, EXTRA_FIRM
    }
}
