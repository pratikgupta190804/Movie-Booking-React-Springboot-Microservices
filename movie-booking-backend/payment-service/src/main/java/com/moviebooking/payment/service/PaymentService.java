package com.moviebooking.payment.service;

import com.moviebooking.payment.dtos.*;

import java.util.List;


public interface PaymentService {

    PaymentOrderResponseDto createPaymentOrder(PaymentOrderRequestDto request, String userId);

    PaymentVerificationResponseDto verifyPayment(PaymentVerificationRequestDto request, String userId);

    void handleWebhook(String payload, String signature);

    void cancelPayment(String orderId, String userId);

    PaymentResponseDto getPaymentById(String paymentId);

    PaymentResponseDto getPaymentByOrderId(String orderId);

    List<PaymentResponseDto> getUserPayments(String userId);

    RefundResponseDto refundPayment(RefundRequestDto request, String userId);

    RefundResponseDto getRefundStatus(String refundId);
}
