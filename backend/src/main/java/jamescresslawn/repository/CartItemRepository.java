package jamescresslawn.repository;

import jamescresslawn.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    // Get all cart items for a specific user
    List<CartItem> findByUserId(String userId);

    // Find a specific product in a specific user's cart
    Optional<CartItem> findByUserIdAndProductId(String userId, String productId);

    // Delete all cart items for a user (called after order is placed)
    void deleteByUserId(String userId);
}
