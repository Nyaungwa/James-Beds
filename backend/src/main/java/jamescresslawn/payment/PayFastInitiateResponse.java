package jamescresslawn.payment;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * What the backend sends back to the React frontend
 * when the user wants to pay for an order.
 *
 * The frontend uses this to build an HTML form and
 * submit it to PayFast, redirecting the user to the payment page.
 *
 * paymentUrl:  where to submit the form (PayFast's endpoint)
 * params:      all the form fields PayFast requires
 *
 * Example frontend usage:
 *   const form = document.createElement('form');
 *   form.method = 'POST';
 *   form.action = response.paymentUrl;
 *   Object.entries(response.params).forEach(([key, value]) => {
 *     const input = document.createElement('input');
 *     input.type = 'hidden';
 *     input.name = key;
 *     input.value = value;
 *     form.appendChild(input);
 *   });
 *   document.body.appendChild(form);
 *   form.submit();
 */
@Data
@Builder
public class PayFastInitiateResponse {

    private String paymentUrl;      // https://sandbox.payfast.co.za/eng/process
    private Map<String, String> params;  // all form fields including signature
    private String orderId;
    private String amount;
}