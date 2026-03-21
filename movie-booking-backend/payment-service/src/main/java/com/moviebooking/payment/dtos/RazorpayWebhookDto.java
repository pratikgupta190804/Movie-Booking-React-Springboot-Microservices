package com.moviebooking.payment.dtos;

import lombok.Data;

@Data
public class RazorpayWebhookDto {

    private String event;
    private String payload; // store raw JSON if needed
}