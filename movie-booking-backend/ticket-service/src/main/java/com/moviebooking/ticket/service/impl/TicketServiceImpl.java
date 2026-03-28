package com.moviebooking.ticket.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.moviebooking.ticket.client.BookingServiceClient;
import com.moviebooking.ticket.client.MovieServiceClient;
import com.moviebooking.ticket.client.ShowServiceClient;
import com.moviebooking.ticket.client.TheatreServiceClient;
import com.moviebooking.ticket.document.Ticket;
import com.moviebooking.ticket.document.TicketSeat;
import com.moviebooking.ticket.dtos.PaymentSuccessfulEvent;
import com.moviebooking.ticket.dtos.TicketGeneratedEvent;
import com.moviebooking.ticket.dtos.TicketResponseDto;
import com.moviebooking.ticket.dtos.TicketSeatDto;
import com.moviebooking.ticket.dtos.external.*;
import com.moviebooking.ticket.enums.TicketStatus;
import com.moviebooking.ticket.exception.TicketNotFoundException;
import com.moviebooking.ticket.repository.TicketRepository;
import com.moviebooking.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

// service/impl/TicketServiceImpl.java
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void generateTicket(PaymentSuccessfulEvent event) {
        log.info("Generating ticket for bookingId: {}", event.getBookingId());

        // ── Step 1: Idempotency check ──────────────────────────────────
        // If ticket already exists for this booking — skip
        if (ticketRepository.existsByBookingId(event.getBookingId())) {
            log.warn("Ticket already exists for bookingId: {} — skipping",
                    event.getBookingId());
            return;
        }


        // ── Step 5: Generate QR Code ───────────────────────────────────
        String qrContent = buildQrContent(event);
        String qrCodeBase64 = generateQrCode(qrContent);

        // ── Step 6: Build and save Ticket document ─────────────────────
        Ticket ticket = Ticket.builder()
                .bookingId(event.getBookingId())
                .userId(event.getUserId())
                .bookingReference(event.getBookingReference())
                .showId(event.getShowId())
                .movieName(event.getMovieName())
                .theatreName(event.getTheatreName())
                .screenName(event.getScreenName())
                .showTime(event.getShowTime())
                .paymentId(event.getPaymentId())
                .totalAmount(event.getFinalAmount())
                .qrCode(qrCodeBase64)
                .status(TicketStatus.GENERATED)
                .generatedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ticket = ticketRepository.save(ticket);
        log.info("Ticket saved to MongoDB with ID: {}", ticket.getId());

        // ── Step 7: Publish ticket-generated-event ─────────────────────
        // notification-service listens to this and sends email
        publishTicketGeneratedEvent(ticket, event);
    }


    // ── QR Code generation ─────────────────────────────────────────────

    private String buildQrContent(PaymentSuccessfulEvent event) {
        // QR content is a structured string that the venue scanner reads
        return String.format(
                "BOOKING:%s|REF:%s|USER:%s|SHOW:%s|AMOUNT:%s",
                event.getBookingId(),
                event.getBookingReference(),
                event.getUserId(),
                event.getShowId(),
                event.getFinalAmount()
        );
    }

    private String generateQrCode(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    300,    // width in pixels
                    300     // height in pixels
            );

            // Convert BitMatrix to BufferedImage
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convert BufferedImage to base64 string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            log.info("QR code generated successfully");
            return base64;

        } catch (Exception e) {
            log.error("Failed to generate QR code: {}", e.getMessage(), e);
            // Don't fail ticket generation if QR fails
            return null;
        }
    }

    // ── Publish event ──────────────────────────────────────────────────

    private void publishTicketGeneratedEvent(Ticket ticket,
                                             PaymentSuccessfulEvent event) {
        TicketGeneratedEvent ticketEvent = TicketGeneratedEvent.builder()
                .ticketId(ticket.getId())
                .bookingId(ticket.getBookingId())
                .userId(ticket.getUserId())
                .bookingReference(ticket.getBookingReference())
                .movieName(ticket.getMovieName())
                .theatreName(ticket.getTheatreName())
                .screenName(ticket.getScreenName())
                .showTime(ticket.getShowTime())
                .totalAmount(ticket.getTotalAmount())
                .qrCode(ticket.getQrCode())
                .generatedAt(ticket.getGeneratedAt())
                .build();

        kafkaTemplate.send("ticket-generated-event", ticket.getBookingId(), ticketEvent);
        log.info("Published TicketGeneratedEvent for bookingId: {}", ticket.getBookingId());
    }

    // ── Query methods ──────────────────────────────────────────────────

    @Override
    public TicketResponseDto getTicketByBookingId(String bookingId, String userId) {
        log.info("Fetching ticket for bookingId: {} by userId: {}", bookingId, userId);

        Ticket ticket = ticketRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new TicketNotFoundException(
                        "Ticket not found for bookingId: " + bookingId));

        // Ownership check
        if (!ticket.getUserId().equals(userId)) {
            throw new RuntimeException("Ticket does not belong to this user");
        }

        return mapToResponseDto(ticket);
    }

    // service/impl/TicketServiceImpl.java — add this method
    @Override
    public TicketResponseDto getTicketById(String ticketId, String userId) {
        log.info("Fetching ticket by ticketId: {} for userId: {}", ticketId, userId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(
                        "Ticket not found with ID: " + ticketId));

        if (!ticket.getUserId().equals(userId)) {
            throw new RuntimeException("Ticket does not belong to this user");
        }

        return mapToResponseDto(ticket);
    }

    @Override
    public List<TicketResponseDto> getUserTickets(String userId) {
        log.info("Fetching all tickets for userId: {}", userId);

        return ticketRepository.findByUserIdOrderByGeneratedAtDesc(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelTicket(String bookingId) {
        log.info("Cancelling ticket for bookingId: {}", bookingId);

        ticketRepository.findByBookingId(bookingId).ifPresent(ticket -> {
            ticket.setStatus(TicketStatus.CANCELLED);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
            log.info("Ticket cancelled for bookingId: {}", bookingId);
        });
    }

    // ── Mapper ─────────────────────────────────────────────────────────

    private TicketResponseDto mapToResponseDto(Ticket ticket) {
        List<TicketSeatDto> seatDtos = ticket.getSeats() == null
                ? List.of()
                : ticket.getSeats().stream()
                .map(seat -> TicketSeatDto.builder()
                        .seatId(seat.getSeatId())
                        .seatNumber(seat.getSeatNumber())
                        .rowLabel(seat.getRowLabel())
                        .seatType(seat.getSeatType())
                        .price(seat.getPrice())
                        .build())
                .collect(Collectors.toList());

        return TicketResponseDto.builder()
                .id(ticket.getId())
                .bookingId(ticket.getBookingId())
                .userId(ticket.getUserId())
                .bookingReference(ticket.getBookingReference())
                .movieName(ticket.getMovieName())
                .moviePosterUrl(ticket.getMoviePosterUrl())
                .theatreName(ticket.getTheatreName())
                .theatreAddress(ticket.getTheatreAddress())
                .screenName(ticket.getScreenName())
                .showTime(ticket.getShowTime())
                .language(ticket.getLanguage())
                .format(ticket.getFormat())
                .seats(seatDtos)
                .paymentId(ticket.getPaymentId())
                .totalAmount(ticket.getTotalAmount())
                .qrCode(ticket.getQrCode())
                .status(ticket.getStatus())
                .generatedAt(ticket.getGeneratedAt())
                .build();
    }
}
