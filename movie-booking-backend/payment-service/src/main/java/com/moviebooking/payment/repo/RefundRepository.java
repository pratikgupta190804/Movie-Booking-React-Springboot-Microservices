package com.moviebooking.payment.repo;

import com.moviebooking.payment.entity.Refund;
import com.moviebooking.payment.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, String> {

    List<Refund> findByPaymentId(String paymentId);

    Optional<Refund> findByProviderRefundId(String providerRefundId);

    List<Refund> findByStatus(RefundStatus status);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.payment.id = :paymentId AND r.status = :status")
    BigDecimal sumRefundedAmountByPaymentIdAndStatus(
            @Param("paymentId") String paymentId,
            @Param("status") RefundStatus status
    );
}
