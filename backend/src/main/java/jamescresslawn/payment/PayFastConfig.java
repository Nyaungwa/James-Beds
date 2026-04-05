package jamescresslawn.payment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * PayFastConfig reads all PayFast-related settings from application.properties.
 *
 * Why use @Value instead of hardcoding?
 * - You can swap sandbox credentials for live credentials
 *   by just changing application.properties
 * - Credentials never appear in your source code
 * - In production, these come from environment variables, not the file
 */
@Component
@Getter
public class PayFastConfig {

    @Value("${payfast.merchant-id}")
    private String merchantId;

    @Value("${payfast.merchant-key}")
    private String merchantKey;

    @Value("${payfast.passphrase}")
    private String passphrase;

    @Value("${payfast.sandbox}")
    private boolean sandbox;

    @Value("${payfast.notify-url}")
    private String notifyUrl;

    @Value("${payfast.return-url}")
    private String returnUrl;

    @Value("${payfast.cancel-url}")
    private String cancelUrl;

    /**
     * Returns the correct PayFast payment URL.
     * Sandbox = test environment (no real money)
     * Live = real payments
     */
    public String getPaymentUrl() {
        return sandbox
            ? "https://sandbox.payfast.co.za/eng/process"
            : "https://www.payfast.co.za/eng/process";
    }
}