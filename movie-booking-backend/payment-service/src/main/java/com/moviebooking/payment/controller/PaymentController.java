package com.moviebooking.payment.controller;

import com.moviebooking.payment.dtos.*;
import com.moviebooking.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// PaymentController.java
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    // ── Create payment order ───────────────────────────────────────────────
    // Frontend calls this first to get razorpayOrderId
    @PostMapping("/order")
    public ResponseEntity<PaymentOrderResponseDto> createPaymentOrder(
            @Valid @RequestBody PaymentOrderRequestDto request,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("POST /api/payments/order — userId: {}, bookingId: {}",
                userId, request.getBookingId());

        PaymentOrderResponseDto response = paymentService.createPaymentOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── Verify payment ─────────────────────────────────────────────────────
    // Frontend calls this after Razorpay checkout succeeds
    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponseDto> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequestDto request,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("POST /api/payments/verify — userId: {}, razorpayOrderId: {}",
                userId, request.getRazorpayOrderId());

        PaymentVerificationResponseDto response = paymentService.verifyPayment(request, userId);
        return ResponseEntity.ok(response);
    }

    // ── Webhook ────────────────────────────────────────────────────────────
    // Called by Razorpay server directly — NO JWT, secured by signature only
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        log.info("POST /api/payments/webhook received");
        paymentService.handleWebhook(payload, signature);

        // Always return 200 — if we return non-2xx Razorpay will keep retrying
        return ResponseEntity.ok().build();
    }

    // ── Cancel payment ─────────────────────────────────────────────────────
    // Only works if payment is in CREATED state (user never attempted payment)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelPayment(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("DELETE /api/payments/{} — userId: {}", orderId, userId);

        paymentService.cancelPayment(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    // ── Get payment by internal ID ─────────────────────────────────────────
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @PathVariable String paymentId,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("GET /api/payments/{} — userId: {}", paymentId, jwt.getSubject());

        PaymentResponseDto response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    // ── Get payment by Razorpay order ID ──────────────────────────────────
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(
            @PathVariable String orderId,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("GET /api/payments/order/{} — userId: {}", orderId, jwt.getSubject());

        PaymentResponseDto response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    // ── Get all payments for logged in user ────────────────────────────────
    @GetMapping("/user/history")
    public ResponseEntity<List<PaymentResponseDto>> getUserPayments(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("GET /api/payments/user/history — userId: {}", userId);

        List<PaymentResponseDto> response = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(response);
    }

    // ── Initiate refund ────────────────────────────────────────────────────
    @PostMapping("/refund")
    public ResponseEntity<RefundResponseDto> refundPayment(
            @Valid @RequestBody RefundRequestDto request,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        log.info("POST /api/payments/refund — userId: {}, paymentId: {}",
                userId, request.getPaymentId());

        RefundResponseDto response = paymentService.refundPayment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── Get refund status ──────────────────────────────────────────────────
    @GetMapping("/refund/{refundId}")
    public ResponseEntity<RefundResponseDto> getRefundStatus(
            @PathVariable String refundId,
            @AuthenticationPrincipal Jwt jwt) {

        log.info("GET /api/payments/refund/{} — userId: {}", refundId, jwt.getSubject());

        RefundResponseDto response = paymentService.getRefundStatus(refundId);
        return ResponseEntity.ok(response);
    }
}
