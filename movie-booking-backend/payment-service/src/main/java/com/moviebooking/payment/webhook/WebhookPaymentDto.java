package com.moviebooking.payment.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookPaymentDto {

    @JsonProperty("id")
    private String id;                  // Razorpay payment_id

    @JsonProperty("entity")
    private String entity;              // "payment"

    @JsonProperty("order_id")
    private String orderId;             // Razorpay order_id

    @JsonProperty("amount")
    private Long amount;                // in paise

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("status")
    private String status;              // "captured", "failed"

    @JsonProperty("method")
    private String method;              // "upi", "card", "netbanking" etc.

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("error_reason")
    private String errorReason;
}