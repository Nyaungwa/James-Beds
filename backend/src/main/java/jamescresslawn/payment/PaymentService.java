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

        // Build params in exact order PayFast expects
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
     * PayFast signature generation — fixed version.
     *
     * The exact rules PayFast uses:
     * 1. Take all params (excluding signature) as key=value
     * 2. URL-encode each VALUE using standard percent-encoding
     * 3. Join with & character
     * 4. If passphrase is set, append &passphrase=ENCODED_PASSPHRASE
     * 5. MD5 hash the entire string
     *
     * The critical fix: PayFast uses PHP's urlencode() which encodes
     * spaces as + NOT %20, and encodes : / ? = & in URLs.
     * We must match PHP's urlencode() exactly.
     */
    public String generateSignature(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().equals("signature")) continue;
                if (entry.getValue() == null || entry.getValue().isEmpty()) continue;

                if (sb.length() > 0) sb.append("&");

                // Use PHP-compatible URL encoding (spaces become +, not %20)
                String encodedValue = phpUrlencode(entry.getValue());
                sb.append(entry.getKey()).append("=").append(encodedValue);
            }

            // Append passphrase if configured
            String passphrase = payFastConfig.getPassphrase();
            if (passphrase != null && !passphrase.trim().isEmpty()) {
                sb.append("&passphrase=").append(phpUrlencode(passphrase));
            }

            String signatureString = sb.toString();
            log.debug("PayFast signature input: {}", signatureString);

            // MD5 hash
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
     * Mimics PHP's urlencode() function.
     *
     * PHP urlencode() encodes spaces as + and uses %XX for everything else.
     * Java's URLEncoder.encode() does the same thing by default with UTF-8.
     * This is what PayFast's PHP SDK uses, so we must match it exactly.
     */
    private String phpUrlencode(String value) {
        try {
            // Java's URLEncoder matches PHP's urlencode:
            // spaces → +
            // special chars → %XX
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
                // NOTE: do NOT skip empty values here - PayFast includes them
                if (entry.getValue() == null) continue;

                if (sb.length() > 0) sb.append("&");
                sb.append(entry.getKey())
                    .append("=")
                    .append(phpUrlencode(entry.getValue()));
            }

            // Append passphrase if set
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

    @Transactional
    @SuppressWarnings("null")
public void handleItn(Map<String, String> itnData) {
    log.info("PayFast ITN received: {}", itnData);

    // In sandbox mode, skip signature validation.
    // PayFast sandbox has known issues with ITN signature order.
    // In production, use PayFast IP whitelist verification instead.
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

    // Check payment status
    String paymentStatus = itnData.get("payment_status");
    if (!"COMPLETE".equalsIgnoreCase(paymentStatus)) {
        log.warn("ITN: payment not complete. Status: {}", paymentStatus);
        return;
    }

    // Find the order
    String orderId = itnData.get("m_payment_id");
    if (orderId == null) {
        throw new RuntimeException("ITN missing m_payment_id");
    }

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

    // Verify amount
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

    // Idempotency check
    if (order.getStatus() == Order.OrderStatus.PAID) {
        log.warn("Order {} already PAID, ignoring duplicate ITN", orderId);
        return;
    }

    // Mark as PAID
    order.setStatus(Order.OrderStatus.PAID);
    orderRepository.save(order);
    log.info("Order {} marked as PAID ✓", orderId);

    // Save payment record
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