package jamescresslawn.order;

import jamescresslawn.entity.Order;
import jamescresslawn.entity.OrderItem;
import jamescresslawn.entity.User;
import jamescresslawn.entity.CartItem;
import jamescresslawn.repository.CartItemRepository;
import jamescresslawn.repository.OrderRepository;
import jamescresslawn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    // ─── Helper: get current logged-in user ──────────────────────────
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    /**
     * CREATE ORDER
     *
     * This is the heart of Phase 4. Here is exactly what happens:
     *
     * 1. Load the user's cart from the database
     * 2. Validate the cart is not empty
     * 3. Create a new Order record (status = PENDING)
     * 4. For each CartItem:
     *    a. Create an OrderItem
     *    b. SNAPSHOT: copy productName, unitPrice, quantity into OrderItem
     *       (we do NOT store a reference to the live price)
     *    c. Add OrderItem to the order
     * 5. Calculate the total from OrderItems (not from live product prices)
     * 6. Save Order + all OrderItems in ONE transaction
     *    (if anything fails, NOTHING is saved — all or nothing)
     * 7. Delete all CartItems for this user
     * 8. Return a clean OrderResponse
     *
     * @Transactional is critical here:
     * Without it, if step 7 fails after step 6 succeeds, you'd have
     * an order created BUT the cart still full — corrupt state.
     * With @Transactional, either ALL database operations succeed,
     * or ALL are rolled back. The database stays consistent.
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = getCurrentUser();

        // ── Step 1: Load cart ─────────────────────────────────────────
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        // ── Step 2: Validate cart is not empty ────────────────────────
        if (cartItems.isEmpty()) {
            throw new RuntimeException(
                "Cannot create order: cart is empty. Add items before checking out.");
        }

        // ── Step 3: Create the Order shell (no items yet) ─────────────
        // Status starts as PENDING — it moves to PAID after payment webhook
        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(BigDecimal.ZERO)  // we'll calculate and update below
                .orderItems(new ArrayList<>())
                .build();

        // ── Step 4 & 5: Build OrderItems and calculate total ──────────
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            /*
             * SNAPSHOT EXPLANATION:
             *
             * cartItem.getProduct().getName()          ← live product name (could change)
             * cartItem.getProduct().getEffectivePrice() ← live product price (could change)
             *
             * We copy these values INTO the OrderItem right now.
             * After this point, the OrderItem has its own copy of the name and price.
             * If someone changes the product price tomorrow, this order is unaffected.
             */
            BigDecimal snapshotPrice = cartItem.getProduct().getEffectivePrice();
            String snapshotName = cartItem.getProduct().getName();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)                         // link to parent order
                    .product(cartItem.getProduct())       // reference kept for reporting
                    .productName(snapshotName)            // SNAPSHOT
                    .unitPrice(snapshotPrice)             // SNAPSHOT
                    .quantity(cartItem.getQuantity())
                    .build();

            order.getOrderItems().add(orderItem);

            // Add this item's total to the running total
            total = total.add(orderItem.getSubtotal());
        }

        // ── Step 5: Set the final calculated total ────────────────────
        order.setTotalAmount(total);

        // ── Step 6: Save order + all order items ──────────────────────
        // Because Order has CascadeType.ALL on orderItems,
        // saving the order automatically saves all its OrderItems too.
        // We don't need to save each OrderItem separately.
        Order savedOrder = orderRepository.save(order);
        log.info("Order created: {} | User: {} | Total: {}",
                savedOrder.getId(), user.getEmail(), total);

        // ── Step 7: Clear the cart ────────────────────────────────────
        // The user has checked out — remove all their cart items.
        cartItemRepository.deleteByUserId(user.getId());
        log.debug("Cart cleared for user: {}", user.getEmail());

        // ── Step 8: Return clean response ────────────────────────────
        return OrderResponse.from(savedOrder, "Order placed successfully");
    }

    /**
     * GET ALL ORDERS FOR CURRENT USER
     * Returns order history, newest first.
     */
    public List<OrderResponse> getMyOrders() {
        User user = getCurrentUser();
        List<Order> orders = orderRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        return orders.stream()
                .map(order -> OrderResponse.from(order, null))
                .toList();
    }

    /**
     * GET SINGLE ORDER
     * Returns full order details including all items.
     * Ownership check: users can only see their own orders.
     */
    @SuppressWarnings("null")
    public OrderResponse getOrder(String orderId) {
        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Security: make sure this order belongs to the requesting user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view this order");
        }

        return OrderResponse.from(order, null);
    }
}