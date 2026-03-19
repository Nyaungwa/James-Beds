package jamescresslawn.repository;

import jamescresslawn.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    // Find payment by the order it belongs to
    Optional<Payment> findByOrderId(String orderId);

    // Find by PayFast/Stripe's own reference number (needed for webhook processing)
    Optional<Payment> findByProviderReference(String providerReference);
}
