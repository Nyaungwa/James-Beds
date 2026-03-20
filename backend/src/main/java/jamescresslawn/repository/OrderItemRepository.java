package jamescresslawn.repository;
 
import jamescresslawn.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
 
    // Get all items belonging to a specific order
    List<OrderItem> findByOrderId(String orderId);
}