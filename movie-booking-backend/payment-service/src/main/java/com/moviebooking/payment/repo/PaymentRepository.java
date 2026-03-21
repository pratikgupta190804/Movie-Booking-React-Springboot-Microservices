package com.moviebooking.payment.repo;

import com.moviebooking.payment.entity.Payment;
import com.moviebooking.payment.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByProviderOrderId(String providerOrderId);

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

    Optional<Payment> findByBookingId(String bookingId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(String userId);

    boolean existsByIdempotencyKey(String idempotencyKey);

    boolean existsByWebhookEventId(String webhookEventId);

    List<Payment> findByStatus(PaymentStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.providerOrderId = :providerOrderId")
    Optional<Payment> findByProviderOrderIdWithLock(
            @Param("providerOrderId") String providerOrderId
    );

    @Query("SELECT p FROM Payment p WHERE p.userId = :userId AND p.status = :status")
    List<Payment> findByUserIdAndStatus(
            @Param("userId") String userId,
            @Param("status") PaymentStatus status
    );
}
