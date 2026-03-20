package jamescresslawn.order;

import lombok.Data;

/**
 * What the frontend sends when placing an order.
 *
 * shippingAddress is optional for now — you can make it required later
 * when you build the checkout form on the frontend.
 *
 * We don't need a list of items here because the backend
 * fetches the cart directly from the database using the JWT token.
 * The user can't tamper with prices by sending their own item list.
 */
@Data
public class CreateOrderRequest {

    private String shippingAddress;  // e.g. "123 Main St, Pretoria, 0001"
}