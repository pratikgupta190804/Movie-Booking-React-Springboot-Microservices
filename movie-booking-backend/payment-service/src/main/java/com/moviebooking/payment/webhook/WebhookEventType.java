package com.moviebooking.payment.webhook;

public enum WebhookEventType {
    PAYMENT_CAPTURED("payment.captured"),       // payment success
    PAYMENT_FAILED("payment.failed"),           // payment failed
    REFUND_PROCESSED("refund.processed"),       // refund done
    REFUND_FAILED("refund.failed"),             // refund failed
    UNKNOWN("unknown");

    private final String eventName;

    WebhookEventType(String eventName) {
        this.eventName = eventName;
    }

    public static WebhookEventType fromString(String event) {
        for (WebhookEventType type : values()) {
            if (type.eventName.equals(event)) return type;
        }
        return UNKNOWN;
    }
}