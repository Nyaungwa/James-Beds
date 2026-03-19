package jamescresslawn.repository;


import jamescresslawn.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    // Get all orders for a user, newest first
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    // Find orders by status (useful for admin: show all PENDING orders)
    List<Order> findByStatus(Order.OrderStatus status);
}
