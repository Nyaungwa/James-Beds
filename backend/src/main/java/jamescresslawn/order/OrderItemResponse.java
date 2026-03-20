package jamescresslawn.order;

import jamescresslawn.entity.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Clean JSON-safe representation of one line item in an order.
 *
 * Notice: we use the SNAPSHOTTED values from OrderItem (productName, unitPrice)
 * NOT the live product data. This is the whole point of snapshotting —
 * even if the product is deleted or repriced, this order item still shows
 * exactly what the customer paid for.
 */
@Data
@Builder
public class OrderItemResponse {

    private String id;
    private String productId;       // kept for reference, but price comes from snapshot
    private String productName;     // SNAPSHOT: copied at order time
    private BigDecimal unitPrice;   // SNAPSHOT: copied at order time
    private int quantity;
    private BigDecimal itemTotal;   // unitPrice × quantity

    public static OrderItemResponse from(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())          // from snapshot field
                .unitPrice(item.getUnitPrice())              // from snapshot field
                .quantity(item.getQuantity())
                .itemTotal(item.getSubtotal())               // uses the entity helper method
                .build();
    }
}