package jamescresslawn.cart;

import jamescresslawn.entity.CartItem;
import jamescresslawn.entity.Product;
import jamescresslawn.entity.User;
import jamescresslawn.repository.CartItemRepository;
import jamescresslawn.repository.ProductRepository;
import jamescresslawn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Gets the currently logged-in user from the JWT token.
     * The JwtAuthFilter already validated the token and stored the email
     * in Spring's SecurityContextHolder before this method is ever called.
     */
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    /**
     * Returns all cart items + computed total for the logged-in user.
     */
    public Map<String, Object> getCart() {
        User user = getCurrentUser();
        List<CartItem> items = cartItemRepository.findByUserId(user.getId());

        List<CartItemResponse> responses = items.stream()
                .map(CartItemResponse::from)
                .toList();

        BigDecimal total = responses.stream()
                .map(CartItemResponse::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = responses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return Map.of(
                "items", responses,
                "total", total,
                "itemCount", itemCount
        );
    }

    /**
     * Adds a product to the cart.
     * Key rule: if the product is already in the cart, increase quantity.
     * Never create duplicate rows for the same product+user combination.
     */
    @Transactional
    @SuppressWarnings("null")
    public CartItemResponse addToCart(AddToCartRequest request) {
        User user = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException(
                        "Product not found: " + request.getProductId()));

        if (!product.getInStock()) {
            throw new RuntimeException("Product is out of stock: " + product.getName());
        }

        // Check if already in cart
        Optional<CartItem> existing = cartItemRepository
                .findByUserIdAndProductId(user.getId(), product.getId());

        CartItem cartItem;
        if (existing.isPresent()) {
            // Increase quantity
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            log.debug("Increased cart quantity for: {} (user: {})",
                    product.getName(), user.getEmail());
        } else {
            // New cart entry
            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            log.debug("Added to cart: {} (user: {})", product.getName(), user.getEmail());
        }

        return CartItemResponse.from(cartItemRepository.save(cartItem));
    }

    /**
     * Changes the quantity of a cart item.
     * Includes ownership check — users can only edit their own cart.
     */
    @Transactional
    @SuppressWarnings("null")
    public CartItemResponse updateQuantity(String cartItemId, UpdateCartRequest request) {
        User user = getCurrentUser();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to update this cart item");
        }

        cartItem.setQuantity(request.getQuantity());
        return CartItemResponse.from(cartItemRepository.save(cartItem));
    }

    /**
     * Removes a single cart item.
     * Includes ownership check.
     */
    @Transactional
    @SuppressWarnings("null")
    public void removeFromCart(String cartItemId) {
        User user = getCurrentUser();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to remove this cart item");
        }

        cartItemRepository.delete(cartItem);
        log.debug("Removed cart item: {} (user: {})", cartItemId, user.getEmail());
    }

    /**
     * Clears entire cart for the current user.
     * Called after a successful order is placed.
     */
    @Transactional
    public void clearCart() {
        User user = getCurrentUser();
        cartItemRepository.deleteByUserId(user.getId());
        log.debug("Cart cleared for user: {}", user.getEmail());
    }
}