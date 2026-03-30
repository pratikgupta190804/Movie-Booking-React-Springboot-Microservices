package com.moviebooking.notification.listener;

import com.moviebooking.notification.dtos.TicketGeneratedEvent;
import com.moviebooking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "ticket-generated-event", groupId = "notification-service-group")
    public void sendNotification(TicketGeneratedEvent dto){
        log.info("Request to send notification for ticketId={} to email={}",
                dto.getTicketId(), dto.getUserEmail());
        try{
            notificationService.sendMail(dto);
            log.info("Mail send successfully to user: "+ dto.getUserEmail());
        } catch (Exception e){
            log.info("Failed to send Mail to user: {} ; and Error: {}", dto.getUserEmail(), e.getMessage());
        }
    }
}
