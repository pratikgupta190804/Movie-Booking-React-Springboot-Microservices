package com.moviebooking.payment.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookRefundDto {

    @JsonProperty("id")
    private String id;                  // Razorpay refund_id

    @JsonProperty("entity")
    private String entity;              // "refund"

    @JsonProperty("payment_id")
    private String paymentId;           // Razorpay payment_id

    @JsonProperty("amount")
    private Long amount;                // in paise

    @JsonProperty("status")
    private String status;              // "processed", "failed"
}