package com.moviebooking.booking.service.impl;

import com.moviebooking.booking.client.InventoryServiceClient;
import com.moviebooking.booking.client.MovieServiceClient;
import com.moviebooking.booking.client.ShowServiceClient;
import com.moviebooking.booking.client.TheatreServiceClient;
import com.moviebooking.booking.dtos.*;
import com.moviebooking.booking.dtos.external.MovieDto;
import com.moviebooking.booking.dtos.external.ScreenDto;
import com.moviebooking.booking.dtos.external.ShowDto;
import com.moviebooking.booking.dtos.external.TheatreDto;
import com.moviebooking.booking.entity.Booking;
import com.moviebooking.booking.entity.BookingSeat;
import com.moviebooking.booking.enums.BookingStatus;
import com.moviebooking.booking.exception.BookingNotFoundException;
import com.moviebooking.booking.exception.UnauthorizedAccessException;
import com.moviebooking.booking.repo.BookingRepository;
import com.moviebooking.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final MovieServiceClient movieServiceClient;
    private final ShowServiceClient showServiceClient;
    private final TheatreServiceClient theatreServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Transactional
    public BookingResponseDto createBooking(CreateBookingRequestDto request) {
        log.info("Creating booking for user: {} and show: {}", request.getUserId(), request.getShowId());

        try {
            // 1. Fetch show details
            ShowDto show = showServiceClient.getShowById(request.getShowId());
            
            // 2. Fetch movie details
            MovieDto movie = movieServiceClient.getMovieById(show.getMovieId());
            
            // 3. Fetch theatre and screen details
            TheatreDto theatre = theatreServiceClient.getTheatreById(show.getTheatreId());
            ScreenDto screen = theatreServiceClient.getScreenById(show.getScreenId());
            
            // 4. Lock seats in inventory service
            SeatLockRequestDto lockRequest = new SeatLockRequestDto();
            lockRequest.setShowId(request.getShowId());
            lockRequest.setUserId(request.getUserId());
            lockRequest.setSeatIds(request.getSeats().stream()
                    .map(SeatBookingDto::getSeatId)
                    .collect(Collectors.toList()));
            lockRequest.setIdempotencyKey(request.getIdempotencyKey());
            
            SeatLockResponseDto lockResponse = inventoryServiceClient.lockSeats(lockRequest);
            
            // 5. Create booking entity
            Booking booking = new Booking();
            booking.setUserId(request.getUserId());
            booking.setShowId(request.getShowId());
            booking.setMovieId(show.getMovieId());
            booking.setTheatreId(show.getTheatreId());
            booking.setScreenId(show.getScreenId());
            booking.setMovieName(movie.getTitle());
            booking.setTheatreName(theatre.getName());
            booking.setScreenName(screen.getName());
            booking.setShowTime(show.getStartTime());
            booking.setStatus(BookingStatus.PENDING);
            booking.setBookingDate(LocalDateTime.now());
            booking.setExpiryTime(lockResponse.getLockExpiresAt());
            booking.setBookingReference(generateBookingReference());
            
            // 6. Add seats to booking
            for (SeatBookingDto seatDto : request.getSeats()) {
                BookingSeat bookingSeat = new BookingSeat();
                bookingSeat.setSeatId(seatDto.getSeatId());
                bookingSeat.setSeatNumber(seatDto.getSeatNumber());
                bookingSeat.setRowNumber(seatDto.getRowNumber());
                bookingSeat.setSeatNumberInRow(seatDto.getSeatNumberInRow());
                bookingSeat.setSeatType(seatDto.getSeatType());
                bookingSeat.setPrice(seatDto.getPrice());
                booking.addSeat(bookingSeat);
            }
            
            // 7. Calculate totals
            booking.calculateTotals();
            
            // 8. Save booking
            booking = bookingRepository.save(booking);
            log.info("Booking created successfully with ID: {}", booking.getId());
            
            // 9. Publish booking created event
            BookingCreatedEvent event = new BookingCreatedEvent(
                    booking.getId(),
                    booking.getUserId(),
                    booking.getShowId(),
                    lockRequest.getSeatIds()
            );
            kafkaTemplate.send("booking-created-event", event);
            log.info("Published BookingCreatedEvent for booking: {}", booking.getId());
            
            // 10. Return response
            return mapToResponseDto(booking, "Booking created successfully. Please complete payment within 10 minutes.");
            
        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create booking: " + e.getMessage());
        }
    }

    @Override
    public BookingResponseDto getBookingById(String bookingId, String userId) {
        log.info("Fetching booking: {} for user: {}", bookingId, userId);
        
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));
        
        return mapToResponseDto(booking, null);
    }

    @Override
    public List<BookingHistoryDto> getUserBookingHistory(String userId) {
        log.info("Fetching booking history for user: {}", userId);
        
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return bookings.stream()
                .map(this::mapToHistoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDto cancelBooking(String bookingId, String userId) {
        log.info("Cancelling booking: {} for user: {}", bookingId, userId);
        
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + bookingId));
        
        // Only allow cancellation for PENDING or CONFIRMED bookings
        if (booking.getStatus() == BookingStatus.CANCELLED || 
            booking.getStatus() == BookingStatus.EXPIRED) {
            throw new IllegalStateException("Booking is already cancelled or expired");
        }
        
        // Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        
        // Publish booking cancelled event (to release seats in inventory)
        BookingCancelledEvent event = new BookingCancelledEvent(
                booking.getId(),
                booking.getUserId(),
                booking.getShowId(),
                booking.getSeats().stream()
                        .map(BookingSeat::getSeatId)
                        .collect(Collectors.toList()),
                "User cancelled"
        );
        kafkaTemplate.send("booking-cancelled-event", event);
        log.info("Published BookingCancelledEvent for booking: {}", booking.getId());
        
        return mapToResponseDto(booking, "Booking cancelled successfully");
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessfulEvent event) {
        log.info("Handling payment success for booking: {}", event.getBookingId());
        
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + event.getBookingId()));
        
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentId(event.getPaymentId());
        booking.setTransactionId(event.getTransactionId());
        bookingRepository.save(booking);
        
        log.info("Booking {} confirmed after successful payment", event.getBookingId());
    }

    @Override
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Handling payment failure for booking: {}", event.getBookingId());
        
        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + event.getBookingId()));
        
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        
        // Publish booking cancelled event (to release seats in inventory)
        BookingCancelledEvent cancelEvent = new BookingCancelledEvent(
                booking.getId(),
                booking.getUserId(),
                booking.getShowId(),
                booking.getSeats().stream()
                        .map(BookingSeat::getSeatId)
                        .collect(Collectors.toList()),
                "Payment failed: " + event.getReason()
        );
        kafkaTemplate.send("booking-cancelled-event", cancelEvent);
        
        log.info("Booking {} cancelled after payment failure", event.getBookingId());
    }

    @Override
    @Transactional
    public void expireBookingsScheduler() {
        log.info("Running booking expiry scheduler");
        
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(
                BookingStatus.PENDING, 
                LocalDateTime.now()
        );
        
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            
            // Publish booking cancelled event (to release seats in inventory)
            BookingCancelledEvent event = new BookingCancelledEvent(
                    booking.getId(),
                    booking.getUserId(),
                    booking.getShowId(),
                    booking.getSeats().stream()
                            .map(BookingSeat::getSeatId)
                            .collect(Collectors.toList()),
                    "Booking expired"
            );
            kafkaTemplate.send("booking-cancelled-event", event);
            
            log.info("Expired booking: {}", booking.getId());
        }
        
        log.info("Expired {} bookings", expiredBookings.size());
    }

    private BookingResponseDto mapToResponseDto(Booking booking, String message) {
        List<SeatBookingDto> seatDtos = booking.getSeats().stream()
                .map(seat -> {
                    SeatBookingDto dto = new SeatBookingDto();
                    dto.setSeatId(seat.getSeatId());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setRowNumber(seat.getRowNumber());
                    dto.setSeatNumberInRow(seat.getSeatNumberInRow());
                    dto.setSeatType(seat.getSeatType());
                    dto.setPrice(seat.getPrice());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return BookingResponseDto.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .userId(booking.getUserId())
                .showId(booking.getShowId())
                .movieId(booking.getMovieId())
                .movieName(booking.getMovieName())
                .theatreName(booking.getTheatreName())
                .screenName(booking.getScreenName())
                .showTime(booking.getShowTime())
                .seats(seatDtos)
                .totalAmount(booking.getTotalAmount())
                .convenienceFee(booking.getConvenienceFee())
                .totalTax(booking.getTotalTax())
                .finalAmount(booking.getFinalAmount())
                .status(booking.getStatus())
                .paymentId(booking.getPaymentId())
                .transactionId(booking.getTransactionId())
                .bookingDate(booking.getBookingDate())
                .expiryTime(booking.getExpiryTime())
                .message(message)
                .build();
    }

    private BookingHistoryDto mapToHistoryDto(Booking booking) {
        return BookingHistoryDto.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .movieName(booking.getMovieName())
                .theatreName(booking.getTheatreName())
                .screenName(booking.getScreenName())
                .showTime(booking.getShowTime())
                .numberOfSeats(booking.getSeats().size())
                .finalAmount(booking.getFinalAmount())
                .status(booking.getStatus().toString())
                .bookingDate(booking.getBookingDate())
                .build();
    }

    private String generateBookingReference() {
        String prefix = "BK";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomNum = String.format("%04d", new Random().nextInt(10000));
        String reference = prefix + timestamp.substring(timestamp.length() - 8) + randomNum;
        
        // Check if reference already exists (very unlikely)
        while (bookingRepository.existsByBookingReference(reference)) {
            randomNum = String.format("%04d", new Random().nextInt(10000));
            reference = prefix + timestamp.substring(timestamp.length() - 8) + randomNum;
        }
        
        return reference;
    }
}
