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

/**
 * Service layer for order management business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    /**
     * Creates a new order from the current user's cart, then clears the cart.
     * Product names and prices are snapshotted into each {@link OrderItem} at creation
     * time so that future product changes do not affect historical order records.
     * The entire operation is transactional — a failure at any step rolls back all changes.
     *
     * @param request optional shipping address
     * @return the created order as an {@link OrderResponse}
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = getCurrentUser();

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException(
                "Cannot create order: cart is empty. Add items before checking out.");
        }

        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .orderItems(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            /*
             * Snapshot the product name and effective price at order time.
             * OrderItems store their own copy so that later product edits
             * or deletions do not retroactively affect this order.
             */
            BigDecimal snapshotPrice = cartItem.getProduct().getEffectivePrice();
            String snapshotName = cartItem.getProduct().getName();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .productName(snapshotName)
                    .unitPrice(snapshotPrice)
                    .quantity(cartItem.getQuantity())
                    .build();

            order.getOrderItems().add(orderItem);
            total = total.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(total);

        // CascadeType.ALL on orderItems means saving the order persists all items automatically.
        Order savedOrder = orderRepository.save(order);
        log.info("Order created: {} | User: {} | Total: {}",
                savedOrder.getId(), user.getEmail(), total);

        cartItemRepository.deleteByUserId(user.getId());
        log.debug("Cart cleared for user: {}", user.getEmail());

        return OrderResponse.from(savedOrder, "Order placed successfully");
    }

    /**
     * Returns all orders for the currently authenticated user, newest first.
     *
     * @return list of {@link OrderResponse} objects
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
     * Returns a single order by ID, verified to belong to the current user.
     *
     * @param orderId the order UUID
     * @return the matching {@link OrderResponse}
     */
    @SuppressWarnings("null")
    public OrderResponse getOrder(String orderId) {
        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view this order");
        }

        return OrderResponse.from(order, null);
    }
}
