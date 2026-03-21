package com.moviebooking.payment.enums;

public enum RefundStatus {
    INITIATED,    // refund request created
    PENDING,      // sent to gateway, awaiting processing
    PROCESSED,    // gateway confirmed refund
    FAILED        // refund failed at gateway
}