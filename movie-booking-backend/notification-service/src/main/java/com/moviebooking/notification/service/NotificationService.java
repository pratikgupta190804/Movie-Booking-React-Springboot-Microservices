package com.moviebooking.notification.service;

import com.moviebooking.notification.dtos.TicketGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendMail(TicketGeneratedEvent dto){
        String msgBody = String.format(
                "Hello,\n\nYour ticket with ID %s has been successfully booked.\n\nEnjoy your movie! 🍿\n\nTicket Link: http://localhost:3000/ticket/%s",
                dto.getTicketId(), dto.getTicketId()
        );;
        String subject = "Your Ticket is Confirmed";
        try {
            SimpleMailMessage mailMessage =
                    new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(dto.getUserEmail());
            mailMessage.setText(msgBody);
            mailMessage.setSubject(subject);

            mailSender.send(mailMessage);

            log.info("Mail Sent Successfully");

        } catch (Exception e) {
            log.error("Failed to send mail to {}. Error: {}",
                    dto.getUserEmail(), e.getMessage());
            throw e;
        }
    }
}
