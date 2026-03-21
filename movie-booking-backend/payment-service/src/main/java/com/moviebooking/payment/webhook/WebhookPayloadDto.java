package com.moviebooking.payment.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)  // ignore fields we don't need
public class WebhookPayloadDto {

    @JsonProperty("entity")
    private String entity;              // "event"

    @JsonProperty("account_id")
    private String accountId;

    @JsonProperty("event")
    private String event;               // "payment.captured", "payment.failed" etc.

    @JsonProperty("contains")
    private List<String> contains;      // ["payment"] or ["refund"]

    @JsonProperty("payload")
    private WebhookPayloadData payload;

    @JsonProperty("created_at")
    private Long createdAt;             // unix timestamp
}