package com.moviebooking.payment.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookPayloadData {

    @JsonProperty("payment")
    private WebhookEntityWrapper<WebhookPaymentDto> payment;

    @JsonProperty("refund")
    private WebhookEntityWrapper<WebhookRefundDto> refund;
}