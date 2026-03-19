package jamescresslawn.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // "order" is a reserved SQL word, so we use "orders"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order goes through these statuses:
    // PENDING (created) → PAID (payment confirmed) → PROCESSING → SHIPPED →
    // DELIVERED
    // CANCELLED is also possible at any point before SHIPPED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    // Total amount at the time of ordering - never recalculate this from products
    // Product prices can change but this order was for THIS amount
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Full shipping address as a single text block for simplicity
    // In a bigger system you'd break this into street, city, postal code etc.
    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    // Set by you (the admin) when you've shipped the order
    @Column(name = "tracking_number")
    private String trackingNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One order contains many order items
    // CascadeType.ALL = saving/deleting an order also saves/deletes its items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    // One order has one payment record
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    public enum OrderStatus {
        PENDING, // Order created, waiting for payment
        PAID, // Payment confirmed via webhook
        PROCESSING, // You've received and are preparing the order
        SHIPPED, // On its way (tracking number added)
        DELIVERED, // Customer received it
        CANCELLED // Order was cancelled
    }
}
