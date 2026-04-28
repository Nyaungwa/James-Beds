package jamescresslawn.payment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for PayFast payment operations.
 * Exposes the payment initiation endpoint and the ITN webhook receiver.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/payments/payfast/{orderId}
     *
     * Called by the React frontend when user clicks "Pay Now".
     * Returns the PayFast payment URL and all required form parameters.
     *
     * The frontend uses this to redirect the user to PayFast.
     *
     * Requires: JWT token (user must be logged in)
     *
     * Response:
     * {
     *   "paymentUrl": "https://sandbox.payfast.co.za/eng/process",
     *   "orderId": "uuid",
     *   "amount": "42998.00",
     *   "params": {
     *     "merchant_id": "10000100",
     *     "merchant_key": "46f0cd694581a",
     *     "return_url": "http://localhost:5173/payment-success?orderId=...",
     *     "cancel_url": "http://localhost:5173/payment-cancel?orderId=...",
     *     "notify_url": "https://abc.ngrok.io/webhooks/payfast",
     *     "m_payment_id": "uuid",
     *     "amount": "42998.00",
     *     "item_name": "James Cresslawn Order #a305a336",
     *     "email_address": "edwin@test.com",
     *     "signature": "abc123..."
     *   }
     * }
     */
    @PostMapping("/api/payments/payfast/{orderId}")
    public ResponseEntity<?> initiatePayment(@PathVariable String orderId) {
        try {
            PayFastInitiateResponse response = paymentService.initiatePayment(orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Payment initiation failed for order {}: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /webhooks/payfast
     *
     * This is the ITN (Instant Transaction Notification) endpoint.
     * PayFast calls this URL directly after a payment is processed.
     *
     * IMPORTANT NOTES:
     * 1. This endpoint MUST be publicly accessible (that's what ngrok is for)
     * 2. This endpoint MUST NOT require authentication (PayFast has no JWT)
     * 3. This MUST return HTTP 200 — PayFast retries failed webhooks
     * 4. Never mark an order as paid from the frontend — only from here
     *
     * PayFast sends form data (not JSON), so we use HttpServletRequest
     * to read all parameters manually.
     *
     * Example ITN payload from PayFast:
     * {
     *   "m_payment_id": "order-uuid",
     *   "pf_payment_id": "payfast-transaction-id",
     *   "payment_status": "COMPLETE",
     *   "item_name": "James Cresslawn Order #a305a336",
     *   "amount_gross": "42998.00",
     *   "amount_fee": "-1234.00",
     *   "amount_net": "41764.00",
     *   "signature": "abc123..."
     * }
     */
    @PostMapping("/webhooks/payfast")
    public ResponseEntity<String> handleItn(HttpServletRequest request) {
        try {
            // PayFast sends application/x-www-form-urlencoded, not JSON.
            Map<String, String> itnData = new HashMap<>();
            request.getParameterMap().forEach((key, values) -> {
                if (values != null && values.length > 0) {
                    itnData.put(key, values[0]);
                }
            });

            log.info("PayFast ITN received with {} parameters", itnData.size());

            paymentService.handleItn(itnData);

            // PayFast retries the ITN if we return anything other than 200.
            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("PayFast ITN processing failed: {}", e.getMessage(), e);
            // Return 200 even on internal errors to prevent PayFast retry spam.
            return ResponseEntity.ok("OK");
        }
    }
}