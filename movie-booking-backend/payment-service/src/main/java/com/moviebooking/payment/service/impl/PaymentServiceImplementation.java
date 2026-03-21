package com.moviebooking.payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.payment.client.BookingServiceClient;
import com.moviebooking.payment.config.RazorpayProperties;
import com.moviebooking.payment.dtos.external.BookingResponseDto;
import com.moviebooking.payment.dtos.external.BookingStatus;
import com.moviebooking.payment.entity.Payment;
import com.moviebooking.payment.entity.Refund;
import com.moviebooking.payment.enums.PaymentMethod;
import com.moviebooking.payment.enums.PaymentProvider;
import com.moviebooking.payment.enums.PaymentStatus;
import com.moviebooking.payment.enums.RefundStatus;
import com.moviebooking.payment.exception.BookingValidationException;
import com.moviebooking.payment.exception.InvalidPaymentStateException;
import com.moviebooking.payment.exception.PaymentNotFoundException;
import com.moviebooking.payment.exception.PaymentVerificationException;
import com.moviebooking.payment.repo.PaymentRepository;
import com.moviebooking.payment.repo.RefundRepository;
import com.moviebooking.payment.dtos.*;
import com.moviebooking.payment.service.PaymentService;
import com.moviebooking.payment.webhook.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final BookingServiceClient bookingServiceClient;
    private final RazorpayClient razorpayClient;
    private final RazorpayProperties razorpayProperties;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebhookSignatureVerifier webhookSignatureVerifier;  // ← add this
    private final ObjectMapper objectMapper;

    @Value("${payment.razorpay.key-id}")
    private String razorpayKeyId;

    @Override
    @Transactional
    public PaymentOrderResponseDto createPaymentOrder(PaymentOrderRequestDto request, String userId) {
        log.info("Creating payment order for booking: {} by user: {}", request.getBookingId(), userId);

        // ── Step 1: Idempotency check ──────────────────────────────────────
        // If same request is sent twice (network retry), return existing payment
        Optional<Payment> existingPayment = paymentRepository
                .findByIdempotencyKey(request.getIdempotencyKey());

        if (existingPayment.isPresent()) {
            log.info("Duplicate request — returning existing payment for idempotency key: {}",
                    request.getIdempotencyKey());
            return mapToOrderResponseDto(existingPayment.get());
        }

        // ── Step 2: Validate booking ───────────────────────────────────────
        BookingResponseDto booking = fetchAndValidateBooking(
                request.getBookingId(), userId, request.getAmount()
        );

        // ── Step 3: Create Razorpay order ──────────────────────────────────
        String razorpayOrderId = createRazorpayOrder(request.getAmount(), request.getCurrency(),
                request.getIdempotencyKey());

        // ── Step 4: Save payment entity ────────────────────────────────────
        Payment payment = Payment.builder()
                .userId(userId)
                .bookingId(request.getBookingId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .status(PaymentStatus.CREATED)
                .provider(PaymentProvider.RAZORPAY)
                .providerOrderId(razorpayOrderId)
                .idempotencyKey(request.getIdempotencyKey())
                .webhookProcessed(false)
                .isDeleted(false)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment entity saved with ID: {} and Razorpay order: {}",
                payment.getId(), razorpayOrderId);

        return mapToOrderResponseDto(payment);
    }

    @Override
    @Transactional
    public PaymentVerificationResponseDto verifyPayment(PaymentVerificationRequestDto request, String userId) {
        log.info("Verifying payment for Razorpay order: {} by user: {}",
                request.getRazorpayOrderId(), userId);

        // ── Step 1: Fetch payment by providerOrderId ───────────────────────
        Payment payment = paymentRepository
                .findByProviderOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "No payment found for Razorpay order: " + request.getRazorpayOrderId()
                ));

        // ── Step 2: Ownership check ────────────────────────────────────────
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentVerificationException("Payment does not belong to this user");
        }

        // ── Step 3: State check — prevent double verification ──────────────
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.warn("Payment already verified: {}", payment.getId());
            return mapToVerificationResponseDto(payment, true, "Payment already verified");
        }

        if (payment.getStatus() == PaymentStatus.CANCELLED
                || payment.getStatus() == PaymentStatus.FAILED) {
            throw new InvalidPaymentStateException(
                    "Cannot verify payment in state: " + payment.getStatus()
            );
        }

        boolean isValidSignature = verifyRazorpaySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (isValidSignature) {
            return handleVerificationSuccess(payment, request);
        } else {
            return handleVerificationFailure(payment, "Invalid payment signature");
        }
    }

    @Override
    public void handleWebhook(String payload, String signature) {
        log.info("Received webhook from Razorpay");

        // ── Step 1: Verify signature ───────────────────────────────────────
        // Reject anything not from Razorpay
        if (!webhookSignatureVerifier.verify(payload, signature)) {
            log.warn("Invalid webhook signature — rejecting request");
            throw new PaymentVerificationException("Invalid webhook signature");
        }

        // ── Step 2: Parse payload ──────────────────────────────────────────
        WebhookPayloadDto webhookPayload;
        try {
            webhookPayload = objectMapper.readValue(payload, WebhookPayloadDto.class);
        } catch (Exception e) {
            log.error("Failed to parse webhook payload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse webhook payload");
        }

        // ── Step 3: Deduplicate ────────────────────────────────────────────
        // Razorpay can send same webhook multiple times — guard against that
        String eventId = webhookPayload.getAccountId() + "_" + webhookPayload.getCreatedAt();

        if (paymentRepository.existsByWebhookEventId(eventId)) {
            log.info("Duplicate webhook received, eventId: {} — skipping", eventId);
            return;     // return 200 OK so Razorpay stops retrying
        }

        // ── Step 4: Route to correct handler ──────────────────────────────
        WebhookEventType eventType = WebhookEventType.fromString(webhookPayload.getEvent());
        log.info("Processing webhook event: {}", eventType);

        switch (eventType) {
            case PAYMENT_CAPTURED  -> handlePaymentCapturedWebhook(webhookPayload, eventId);
            case PAYMENT_FAILED    -> handlePaymentFailedWebhook(webhookPayload, eventId);
            case REFUND_PROCESSED  -> handleRefundProcessedWebhook(webhookPayload, eventId);
            case REFUND_FAILED     -> handleRefundFailedWebhook(webhookPayload, eventId);
            default                -> log.warn("Unhandled webhook event type: {}",
                    webhookPayload.getEvent());
        }
    }

    @Override
    @Transactional
    public void cancelPayment(String orderId, String userId) {
        log.info("Cancelling payment for orderId: {} by user: {}", orderId, userId);

        // ── Step 1: Fetch payment ──────────────────────────────────────────
        Payment payment = paymentRepository.findByProviderOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for order ID: " + orderId
                ));

        // ── Step 2: Ownership check ────────────────────────────────────────
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentVerificationException("Payment does not belong to this user");
        }

        // ── Step 3: State check ────────────────────────────────────────────
        // Can only cancel if payment order is CREATED but user hasn't attempted payment yet
        // Once PENDING/SUCCESS/FAILED it's too late to cancel
        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new InvalidPaymentStateException(
                    "Payment cannot be cancelled in state: " + payment.getStatus() +
                            ". Only CREATED payments can be cancelled."
            );
        }

        // ── Step 4: Update status ──────────────────────────────────────────
        // No need to call Razorpay API here — a CREATED order with no payment
        // attempt doesn't need explicit cancellation on Razorpay's side.
        // It will auto-expire on their end.
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        log.info("Payment cancelled successfully for orderId: {}", orderId);
    }

    @Override
    public PaymentResponseDto getPaymentById(String paymentId) {
        log.info("Fetching payment by ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with ID: " + paymentId
                ));

        return mapToPaymentResponseDto(payment);
    }

    @Override
    public PaymentResponseDto getPaymentByOrderId(String orderId) {
        log.info("Fetching payment by orderId: {}", orderId);

        Payment payment = paymentRepository.findByProviderOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found for order ID: " + orderId
                ));

        return mapToPaymentResponseDto(payment);
    }

    @Override
    public List<PaymentResponseDto> getUserPayments(String userId) {
        log.info("Fetching all payments for user: {}", userId);

        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (payments.isEmpty()) {
            log.info("No payments found for user: {}", userId);
            return List.of();
        }

        return payments.stream()
                .map(this::mapToPaymentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RefundResponseDto refundPayment(RefundRequestDto request, String userId) {
        log.info("Initiating refund for payment: {} by user: {}", request.getPaymentId(), userId);

        // ── Step 1: Fetch payment ──────────────────────────────────────────
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found with ID: " + request.getPaymentId()
                ));

        // ── Step 2: Ownership check ────────────────────────────────────────
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentVerificationException("Payment does not belong to this user");
        }

        // ── Step 3: Refundable state check ────────────────────────────────
        if (!payment.isRefundable()) {
            throw new InvalidPaymentStateException(
                    "Payment is not refundable. Current status: " + payment.getStatus()
            );
        }

        // ── Step 4: Refund amount validation ──────────────────────────────
        BigDecimal refundableAmount = payment.getRefundableAmount();

        if (request.getAmount().compareTo(refundableAmount) > 0) {
            throw new InvalidPaymentStateException(
                    "Refund amount exceeds refundable amount. " +
                            "Requested: " + request.getAmount() +
                            ", Refundable: " + refundableAmount
            );
        }

        // ── Step 5: Call Razorpay refund API ──────────────────────────────
        String providerRefundId = initiateRazorpayRefund(
                payment.getProviderPaymentId(),
                request.getAmount()
        );

        // ── Step 6: Save Refund entity ────────────────────────────────────
        Refund refund = Refund.builder()
                .payment(payment)
                .amount(request.getAmount())
                .status(RefundStatus.INITIATED)
                .providerRefundId(providerRefundId)
                .reason(request.getReason())
                .initiatedBy(userId)
                .build();

        refund = refundRepository.save(refund);

        // ── Step 7: Update payment status ─────────────────────────────────
        // Update refundedAmount on payment for tracking
        BigDecimal newRefundedAmount = payment.getRefundedAmount() == null
                ? request.getAmount()
                : payment.getRefundedAmount().add(request.getAmount());

        payment.setRefundedAmount(newRefundedAmount);
        payment.setStatus(PaymentStatus.REFUND_INITIATED);
        paymentRepository.save(payment);

        log.info("Refund initiated successfully. RefundId: {}, ProviderRefundId: {}",
                refund.getId(), providerRefundId);

        return mapToRefundResponseDto(refund);
    }

    // ── Razorpay refund API call ───────────────────────────────────────────
    private String initiateRazorpayRefund(String providerPaymentId, BigDecimal amount) {
        try {
            JSONObject refundRequest = new JSONObject();

            // Convert to paise
            refundRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());

            com.razorpay.Refund razorpayRefund = razorpayClient.payments
                    .refund(providerPaymentId, refundRequest);

            String providerRefundId = razorpayRefund.get("id");
            log.info("Razorpay refund created: {}", providerRefundId);

            return providerRefundId;

        } catch (RazorpayException e) {
            log.error("Failed to initiate refund with Razorpay: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate refund with gateway: " + e.getMessage());
        }
    }

    @Override
    public RefundResponseDto getRefundStatus(String refundId) {
        log.info("Fetching refund status for refundId: {}", refundId);

        // ── Step 1: Fetch refund from DB ───────────────────────────────────
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Refund not found with ID: " + refundId
                ));

        // ── Step 2: If already in terminal state, return from DB ───────────
        // No need to call Razorpay if already PROCESSED or FAILED
        if (refund.getStatus() == RefundStatus.PROCESSED
                || refund.getStatus() == RefundStatus.FAILED) {
            log.info("Refund {} already in terminal state: {}", refundId, refund.getStatus());
            return mapToRefundResponseDto(refund);
        }

        // ── Step 3: Poll Razorpay for latest status ────────────────────────
        // Only poll if status is INITIATED or PENDING
        RefundStatus latestStatus = fetchRefundStatusFromRazorpay(refund.getProviderRefundId());

        // ── Step 4: Update if status changed ──────────────────────────────
        if (latestStatus != refund.getStatus()) {
            log.info("Refund status changed from {} to {} for refundId: {}",
                    refund.getStatus(), latestStatus, refundId);

            refund.setStatus(latestStatus);

            if (latestStatus == RefundStatus.PROCESSED) {
                refund.setProcessedAt(LocalDateTime.now());

                // Update payment refunded amount + status
                Payment payment = refund.getPayment();
                BigDecimal totalRefunded = refundRepository
                        .sumRefundedAmountByPaymentIdAndStatus(
                                payment.getId(), RefundStatus.PROCESSED
                        );

                payment.setRefundedAmount(totalRefunded);
                payment.setStatus(
                        totalRefunded.compareTo(payment.getAmount()) >= 0
                                ? PaymentStatus.FULLY_REFUNDED
                                : PaymentStatus.PARTIALLY_REFUNDED
                );
                paymentRepository.save(payment);
            }

            if (latestStatus == RefundStatus.FAILED) {
                // Revert payment back to SUCCESS since refund didn't go through
                Payment payment = refund.getPayment();
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
            }

            refundRepository.save(refund);
        }

        return mapToRefundResponseDto(refund);
    }

    // ── Poll Razorpay for refund status ───────────────────────────────────
    private RefundStatus fetchRefundStatusFromRazorpay(String providerRefundId) {
        try {
            com.razorpay.Refund razorpayRefund = razorpayClient.refunds.fetch(providerRefundId);
            String status = razorpayRefund.get("status");

            log.info("Razorpay refund status for {}: {}", providerRefundId, status);

            return switch (status) {
                case "processed" -> RefundStatus.PROCESSED;
                case "failed"    -> RefundStatus.FAILED;
                case "pending"   -> RefundStatus.PENDING;
                default          -> RefundStatus.INITIATED;
            };

        } catch (RazorpayException e) {
            log.error("Failed to fetch refund status from Razorpay: {}", e.getMessage(), e);
            // Don't throw — return current status to avoid breaking the flow
            return RefundStatus.INITIATED;
        }
    }

    private BookingResponseDto fetchAndValidateBooking(String bookingId,
                                                       String userId,
                                                       BigDecimal requestedAmount) {
        BookingResponseDto booking;
        try {
            booking = bookingServiceClient.getBookingById(bookingId);
        } catch (Exception e) {
            log.error("Failed to fetch booking: {}", bookingId, e);
            throw new BookingValidationException("Could not fetch booking details: " + bookingId);
        }

        // Booking must belong to this user
        if (!booking.getUserId().equals(userId)) {
            throw new BookingValidationException("Booking does not belong to this user");
        }

        // Booking must be in PENDING state
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingValidationException(
                    "Booking is not in a payable state. Current status: " + booking.getStatus()
            );
        }

        // Booking must not have expired
        if (booking.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BookingValidationException("Booking has expired. Please create a new booking.");
        }

        // Amount must match booking's finalAmount — prevent tampering
        if (requestedAmount.compareTo(booking.getFinalAmount()) != 0) {
            throw new BookingValidationException(
                    "Payment amount does not match booking amount. Expected: "
                            + booking.getFinalAmount() + ", Got: " + requestedAmount
            );
        }

        return booking;
    }

    // ── Razorpay order creation helper ────────────────────────────────────
    private String createRazorpayOrder(BigDecimal amount, String currency, String receipt) {
        try {
            JSONObject orderRequest = new JSONObject();

            // Razorpay expects amount in smallest currency unit (paise for INR)
            // ₹100.00 → 10000 paise
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());
            orderRequest.put("currency", currency.toUpperCase());
            orderRequest.put("receipt", receipt);   // idempotency key as receipt
            orderRequest.put("payment_capture", 1); // auto-capture on payment

            Order order = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = order.get("id");

            log.info("Razorpay order created: {}", razorpayOrderId);
            return razorpayOrderId;

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create payment order with gateway: " + e.getMessage());
        }
    }

    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        try {
            // Razorpay signs: orderId + "|" + paymentId
            String payload = orderId + "|" + paymentId;

            String generatedSignature = Utils.getHash(payload, razorpayProperties.getKeySecret());

            boolean isValid = generatedSignature.equals(signature);
            log.info("Signature verification result: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Error during signature verification: {}", e.getMessage(), e);
            return false;
        }
    }

    // ── On successful verification ─────────────────────────────────────────
    private PaymentVerificationResponseDto handleVerificationSuccess(Payment payment,
                                                                     PaymentVerificationRequestDto request) {
        log.info("Payment verification successful for payment: {}", payment.getId());

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderPaymentId(request.getRazorpayPaymentId());
        payment.setProviderSignature(request.getRazorpaySignature());
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // Publish Kafka event → booking-service will confirm the booking
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .bookingId(payment.getBookingId())
                .paymentId(payment.getId())
                .providerPaymentId(request.getRazorpayPaymentId())
                .amount(payment.getAmount())
                .paidAt(payment.getCompletedAt())
                .build();

        kafkaTemplate.send("payment-success-event", payment.getBookingId(), event);
        log.info("Published PaymentSuccessEvent for booking: {}", payment.getBookingId());

        return mapToVerificationResponseDto(payment, true, "Payment verified successfully");
    }

    private PaymentVerificationResponseDto handleVerificationFailure(Payment payment,
                                                                     String reason) {
        log.warn("Payment verification failed for payment: {}. Reason: {}", payment.getId(), reason);

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        paymentRepository.save(payment);

        // Publish Kafka event → booking-service will cancel the booking
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .bookingId(payment.getBookingId())
                .paymentId(payment.getId())
                .reason(reason)
                .failedAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send("payment-failed-event", payment.getBookingId(), event);
        log.warn("Published PaymentFailedEvent for booking: {}", payment.getBookingId());

        return mapToVerificationResponseDto(payment, false, "Payment verification failed: " + reason);
    }

    // ── Mappers ────────────────────────────────────────────────────────────
    private PaymentOrderResponseDto mapToOrderResponseDto(Payment payment) {
        return PaymentOrderResponseDto.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .razorpayOrderId(payment.getProviderOrderId())
                .razorpayKeyId(razorpayKeyId)   // public key for frontend
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .message("Payment order created. Complete payment within 5 minutes.")
                .build();
    }

    private PaymentVerificationResponseDto mapToVerificationResponseDto(Payment payment,
                                                                        boolean success,
                                                                        String message) {
        return PaymentVerificationResponseDto.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .success(success)
                .status(payment.getStatus())
                .message(message)
                .build();
    }

    // ── payment.captured ──────────────────────────────────────────────────
    private void handlePaymentCapturedWebhook(WebhookPayloadDto webhookPayload, String eventId) {
        WebhookPaymentDto paymentData = webhookPayload.getPayload().getPayment().getEntity();

        log.info("Handling payment.captured webhook for Razorpay order: {}",
                paymentData.getOrderId());

        Payment payment = paymentRepository
                .findByProviderOrderId(paymentData.getOrderId())
                .orElse(null);

        if (payment == null) {
            log.warn("No payment found for Razorpay order: {} — possibly race condition, skipping",
                    paymentData.getOrderId());
            return;
        }

        // If /verify already handled this — don't double process
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment already marked SUCCESS via /verify — updating webhook fields only");
            payment.setWebhookEventId(eventId);
            payment.setWebhookProcessed(true);
            paymentRepository.save(payment);
            return;
        }

        // Webhook got here before /verify (rare but possible)
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderPaymentId(paymentData.getId());
        payment.setMethod(mapPaymentMethod(paymentData.getMethod()));
        payment.setCompletedAt(LocalDateTime.now());
        payment.setWebhookEventId(eventId);
        payment.setWebhookProcessed(true);
        paymentRepository.save(payment);

        // Publish success event
        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                .bookingId(payment.getBookingId())
                .paymentId(payment.getId())
                .providerPaymentId(paymentData.getId())
                .amount(payment.getAmount())
                .paidAt(payment.getCompletedAt())
                .build();

        kafkaTemplate.send("payment-success-event", payment.getBookingId(), event);
        log.info("Published PaymentSuccessEvent via webhook for booking: {}",
                payment.getBookingId());
    }

    // ── payment.failed ────────────────────────────────────────────────────
    private void handlePaymentFailedWebhook(WebhookPayloadDto webhookPayload, String eventId) {
        WebhookPaymentDto paymentData = webhookPayload.getPayload().getPayment().getEntity();

        log.info("Handling payment.failed webhook for Razorpay order: {}",
                paymentData.getOrderId());

        Payment payment = paymentRepository
                .findByProviderOrderId(paymentData.getOrderId())
                .orElse(null);

        if (payment == null) {
            log.warn("No payment found for Razorpay order: {}", paymentData.getOrderId());
            return;
        }

        // Already handled by /verify
        if (payment.getStatus() == PaymentStatus.FAILED
                || payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("Payment already in terminal state: {} — skipping", payment.getStatus());
            payment.setWebhookEventId(eventId);
            payment.setWebhookProcessed(true);
            paymentRepository.save(payment);
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureCode(paymentData.getErrorCode());
        payment.setFailureReason(paymentData.getErrorDescription());
        payment.setWebhookEventId(eventId);
        payment.setWebhookProcessed(true);
        paymentRepository.save(payment);

        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .bookingId(payment.getBookingId())
                .paymentId(payment.getId())
                .reason(paymentData.getErrorDescription())
                .failureCode(paymentData.getErrorCode())
                .failedAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send("payment-failed-event", payment.getBookingId(), event);
        log.info("Published PaymentFailedEvent via webhook for booking: {}",
                payment.getBookingId());
    }

    // ── refund.processed ──────────────────────────────────────────────────
    private void handleRefundProcessedWebhook(WebhookPayloadDto webhookPayload, String eventId) {
        WebhookRefundDto refundData = webhookPayload.getPayload().getRefund().getEntity();

        log.info("Handling refund.processed webhook for Razorpay refund: {}", refundData.getId());

        // Find refund by providerRefundId
        Refund refund = refundRepository
                .findByProviderRefundId(refundData.getId())
                .orElse(null);

        if (refund == null) {
            log.warn("No refund found for Razorpay refund ID: {}", refundData.getId());
            return;
        }

        // Update refund status
        refund.setStatus(RefundStatus.PROCESSED);
        refund.setProcessedAt(LocalDateTime.now());
        refundRepository.save(refund);

        // Update payment status
        Payment payment = refund.getPayment();
        BigDecimal totalRefunded = refundRepository
                .sumRefundedAmountByPaymentIdAndStatus(payment.getId(), RefundStatus.PROCESSED);

        payment.setRefundedAmount(totalRefunded);

        // Full refund vs partial refund
        if (totalRefunded.compareTo(payment.getAmount()) >= 0) {
            payment.setStatus(PaymentStatus.FULLY_REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }

        payment.setWebhookEventId(eventId);
        payment.setWebhookProcessed(true);
        paymentRepository.save(payment);

        log.info("Refund {} processed successfully. Payment {} status updated to {}",
                refundData.getId(), payment.getId(), payment.getStatus());
    }

    // ── refund.failed ─────────────────────────────────────────────────────
    private void handleRefundFailedWebhook(WebhookPayloadDto webhookPayload, String eventId) {
        WebhookRefundDto refundData = webhookPayload.getPayload().getRefund().getEntity();

        log.info("Handling refund.failed webhook for Razorpay refund: {}", refundData.getId());

        Refund refund = refundRepository
                .findByProviderRefundId(refundData.getId())
                .orElse(null);

        if (refund == null) {
            log.warn("No refund found for Razorpay refund ID: {}", refundData.getId());
            return;
        }

        refund.setStatus(RefundStatus.FAILED);
        refundRepository.save(refund);

        // Revert payment status back to SUCCESS since refund failed
        Payment payment = refund.getPayment();
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setWebhookEventId(eventId);
        payment.setWebhookProcessed(true);
        paymentRepository.save(payment);

        log.warn("Refund {} failed for payment: {}", refundData.getId(), payment.getId());
    }

    // ── Payment method mapper ─────────────────────────────────────────────
    private PaymentMethod mapPaymentMethod(String razorpayMethod) {
        if (razorpayMethod == null) return PaymentMethod.UNKNOWN;
        return switch (razorpayMethod.toLowerCase()) {
            case "upi"        -> PaymentMethod.UPI;
            case "card"       -> PaymentMethod.CREDIT_CARD;
            case "netbanking" -> PaymentMethod.NET_BANKING;
            case "wallet"     -> PaymentMethod.WALLET;
            case "emi"        -> PaymentMethod.EMI;
            default           -> PaymentMethod.UNKNOWN;
        };
    }

    private RefundResponseDto mapToRefundResponseDto(Refund refund) {
        return RefundResponseDto.builder()
                .refundId(refund.getId())
                .paymentId(refund.getPayment().getId())
                .amount(refund.getAmount())
                .status(refund.getStatus())
                .providerRefundId(refund.getProviderRefundId())
                .reason(refund.getReason())
                .createdAt(refund.getCreatedAt())
                .processedAt(refund.getProcessedAt())
                .build();
    }

    private PaymentResponseDto mapToPaymentResponseDto(Payment payment) {

        // Map refunds only if present
        List<RefundResponseDto> refundDtos = payment.getRefunds() == null
                ? List.of()
                : payment.getRefunds().stream()
                .map(this::mapToRefundResponseDto)
                .collect(Collectors.toList());

        return PaymentResponseDto.builder()
                .id(payment.getId())
                .userId(payment.getUserId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .provider(payment.getProvider())
                .providerOrderId(payment.getProviderOrderId())
                .providerPaymentId(payment.getProviderPaymentId())
                .refundedAmount(payment.getRefundedAmount())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .completedAt(payment.getCompletedAt())
                .refunds(refundDtos)
                .build();
    }
}
