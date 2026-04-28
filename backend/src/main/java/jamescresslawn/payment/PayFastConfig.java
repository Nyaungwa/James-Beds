package jamescresslawn.payment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the PayFast payment gateway.
 * All values are injected from application.properties and can be overridden
 * via environment variables for production deployments.
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
     * Returns the appropriate PayFast payment URL based on the sandbox flag.
     *
     * @return sandbox or live PayFast payment endpoint
     */
    public String getPaymentUrl() {
        return sandbox
            ? "https://sandbox.payfast.co.za/eng/process"
            : "https://www.payfast.co.za/eng/process";
    }
}