package naitei.group5.workingspacebooking.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookingHistoryResponseDto(
        Integer bookingId,
        String venueName,
        String venueLocation,
        String status,
        LocalDateTime createdAt,
        List<BookingDetailResponseDto> bookingDetails,
        PaymentResponseDto payment
) {}
