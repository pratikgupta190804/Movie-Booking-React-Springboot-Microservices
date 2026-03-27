package com.moviebooking.payment.entity;

import com.moviebooking.payment.enums.PaymentMethod;
import com.moviebooking.payment.enums.PaymentProvider;
import com.moviebooking.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_user_id", columnList = "userId"),
        @Index(name = "idx_payment_booking_id", columnList = "bookingId"),
        @Index(name = "idx_payment_provider_order_id", columnList = "providerOrderId"),
        @Index(name = "idx_payment_idempotency_key", columnList = "idempotencyKey")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String bookingId;

    @Column(nullable = false)
    private String showId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider;

    private String providerOrderId;
    private String providerPaymentId;
    private String providerSignature;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    private BigDecimal refundedAmount;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Refund> refunds = new ArrayList<>();

    private String failureReason;
    private String failureCode;

    @Column(unique = true)
    private String webhookEventId;

    @Builder.Default
    private Boolean webhookProcessed = false;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    @Builder.Default
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isDeleted == null) isDeleted = false;
        if (webhookProcessed == null) webhookProcessed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isSuccessful() {
        return PaymentStatus.SUCCESS.equals(this.status);
    }

    public boolean isRefundable() {
        return PaymentStatus.SUCCESS.equals(this.status) ||
                PaymentStatus.PARTIALLY_REFUNDED.equals(this.status);
    }

    public BigDecimal getRefundableAmount() {
        if (refundedAmount == null) return amount;
        return amount.subtract(refundedAmount);
    }
}