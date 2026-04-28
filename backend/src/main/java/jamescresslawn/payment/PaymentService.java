package jamescresslawn.payment;

import jamescresslawn.entity.Order;
import jamescresslawn.entity.Payment;
import jamescresslawn.entity.User;
import jamescresslawn.repository.OrderRepository;
import jamescresslawn.repository.PaymentRepository;
import jamescresslawn.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service layer for PayFast payment integration.
 * Handles payment initiation and ITN (Instant Transaction Notification) processing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PayFastConfig payFastConfig;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Prepares the payment parameters required to redirect the user to PayFast.
     * Builds the parameter map in the order PayFast requires, computes the MD5 signature,
     * and returns a response containing the payment URL and all form fields.
     *
     * @param orderId the UUID of the order to pay for
     * @return a {@link PayFastInitiateResponse} with the payment URL and signed params
     */
    @SuppressWarnings("null")
    public PayFastInitiateResponse initiatePayment(String orderId) {
        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to pay for this order");
        }

        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new RuntimeException("Order is already paid");
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Order has been cancelled");
        }

        String amount = order.getTotalAmount()
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();

        // Build params in exact order PayFast requires for signature generation.
        Map<String, String> params = new LinkedHashMap<>();
        params.put("merchant_id",  payFastConfig.getMerchantId());
        params.put("merchant_key", payFastConfig.getMerchantKey());
        params.put("return_url",   payFastConfig.getReturnUrl() + "?orderId=" + orderId);
        params.put("cancel_url",   payFastConfig.getCancelUrl() + "?orderId=" + orderId);
        params.put("notify_url",   payFastConfig.getNotifyUrl());

        String[] nameParts = user.getFullName().split(" ", 2);
        params.put("name_first",    nameParts[0]);
        params.put("name_last",     nameParts.length > 1 ? nameParts[1] : "");
        params.put("email_address", user.getEmail());
        params.put("m_payment_id", orderId);
        params.put("amount",       amount);
        params.put("item_name",    "James Cresslawn Order #" + orderId.substring(0, 8));

        String signature = generateSignature(params);
        params.put("signature", signature);

        log.info("PayFast payment initiated | Order: {} | Amount: {}", orderId, amount);

        return PayFastInitiateResponse.builder()
                .paymentUrl(payFastConfig.getPaymentUrl())
                .params(params)
                .orderId(orderId)
                .amount(amount)
                .build();
    }

    /**
     * Generates the MD5 signature required by PayFast for both payment initiation and ITN.
     * <p>
     * The algorithm mirrors PHP's {@code urlencode()} behaviour used by the PayFast PHP SDK:
     * <ol>
     *   <li>Concatenate all params (excluding {@code signature}) as {@code key=value} pairs
     *       joined by {@code &}, with values percent-encoded (spaces as {@code +}).</li>
     *   <li>If a passphrase is configured, append {@code &passphrase=ENCODED_VALUE}.</li>
     *   <li>MD5-hash the resulting string and return the lowercase hex digest.</li>
     * </ol>
     *
     * @param params ordered parameter map (must not contain the signature key)
     * @return lowercase MD5 hex string
     */
    public String generateSignature(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().equals("signature")) continue;
                if (entry.getValue() == null || entry.getValue().isEmpty()) continue;

                if (sb.length() > 0) sb.append("&");
                sb.append(entry.getKey()).append("=").append(phpUrlencode(entry.getValue()));
            }

            String passphrase = payFastConfig.getPassphrase();
            if (passphrase != null && !passphrase.trim().isEmpty()) {
                sb.append("&passphrase=").append(phpUrlencode(passphrase));
            }

            String signatureString = sb.toString();
            log.debug("PayFast signature input: {}", signatureString);

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(signatureString.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            String signature = hexString.toString();
            log.debug("Generated signature: {}", signature);
            return signature;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PayFast signature", e);
        }
    }

    /**
     * URL-encodes a value using PHP's {@code urlencode()} convention
     * (spaces as {@code +}, special characters as {@code %XX}).
     * Java's {@code URLEncoder} with UTF-8 matches this behaviour exactly.
     *
     * @param value the raw string to encode
     * @return the percent-encoded string
     */
    private String phpUrlencode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    private String generateItnSignature(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().equals("signature")) continue;
                // PayFast includes empty values in the ITN signature string.
                if (entry.getValue() == null) continue;

                if (sb.length() > 0) sb.append("&");
                sb.append(entry.getKey())
                    .append("=")
                    .append(phpUrlencode(entry.getValue()));
            }

            String passphrase = payFastConfig.getPassphrase();
            if (passphrase != null && !passphrase.trim().isEmpty()) {
                sb.append("&passphrase=").append(phpUrlencode(passphrase));
            }

            String signatureString = sb.toString();
            log.debug("ITN signature input: {}", signatureString);

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(
                signatureString.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ITN signature", e);
        }
    }

    /**
     * Processes a PayFast ITN (Instant Transaction Notification) webhook.
     * <p>
     * Verification flow:
     * <ol>
     *   <li>In production, validates the MD5 signature against the received params.</li>
     *   <li>Checks {@code payment_status == COMPLETE}.</li>
     *   <li>Resolves the order via {@code m_payment_id}.</li>
     *   <li>Verifies {@code amount_gross} matches the order total (prevents partial payment fraud).</li>
     *   <li>Idempotency guard: skips orders already marked PAID.</li>
     *   <li>Marks the order PAID and persists a {@link Payment} record.</li>
     * </ol>
     * <p>
     * Sandbox mode skips signature validation due to known PayFast sandbox ITN issues.
     * In production, supplement with PayFast IP whitelist verification.
     *
     * @param itnData all form parameters received from PayFast
     */
    @Transactional
    @SuppressWarnings("null")
    public void handleItn(Map<String, String> itnData) {
        log.info("PayFast ITN received: {}", itnData);

        // Sandbox skips signature validation; PayFast sandbox has known ITN parameter-order issues.
        // In production, also enforce PayFast IP whitelist verification.
        if (!payFastConfig.isSandbox()) {
            String receivedSignature = itnData.get("signature");
            if (receivedSignature == null) {
                throw new RuntimeException("ITN missing signature");
            }
            Map<String, String> paramsToVerify = new LinkedHashMap<>(itnData);
            paramsToVerify.remove("signature");
            String expectedSignature = generateItnSignature(paramsToVerify);
            if (!expectedSignature.equalsIgnoreCase(receivedSignature)) {
                log.error("ITN signature mismatch | Expected: {} | Received: {}",
                    expectedSignature, receivedSignature);
                throw new RuntimeException("ITN signature validation failed");
            }
            log.info("ITN signature validated ✓");
        } else {
            log.info("Sandbox mode: skipping ITN signature validation");
        }

        String paymentStatus = itnData.get("payment_status");
        if (!"COMPLETE".equalsIgnoreCase(paymentStatus)) {
            log.warn("ITN: payment not complete. Status: {}", paymentStatus);
            return;
        }

        String orderId = itnData.get("m_payment_id");
        if (orderId == null) {
            throw new RuntimeException("ITN missing m_payment_id");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        String itnAmount = itnData.get("amount_gross");
        if (itnAmount == null) {
            throw new RuntimeException("ITN missing amount_gross");
        }

        BigDecimal paidAmount = new BigDecimal(itnAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal orderAmount = order.getTotalAmount().setScale(2, RoundingMode.HALF_UP);

        if (paidAmount.compareTo(orderAmount) != 0) {
            log.error("ITN AMOUNT MISMATCH | Paid: {} | Expected: {}", paidAmount, orderAmount);
            throw new RuntimeException("Amount mismatch");
        }

        log.info("Amount verified: {} ✓", paidAmount);

        // Idempotency: ignore duplicate ITN notifications for an already-paid order.
        if (order.getStatus() == Order.OrderStatus.PAID) {
            log.warn("Order {} already PAID, ignoring duplicate ITN", orderId);
            return;
        }

        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);
        log.info("Order {} marked as PAID ✓", orderId);

        Payment payment = Payment.builder()
                .order(order)
                .provider(Payment.PaymentProvider.PAYFAST)
                .providerReference(itnData.get("pf_payment_id"))
                .status(Payment.PaymentStatus.COMPLETED)
                .amount(paidAmount)
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        log.info("Payment record saved for order: {}", orderId);
    }
}
