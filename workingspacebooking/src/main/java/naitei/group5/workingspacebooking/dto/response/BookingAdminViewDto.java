package naitei.group5.workingspacebooking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingAdminViewDto(
    Integer bookingId,
    Integer userId,
    String userName,
    String userEmail,
    Integer venueId,
    String venueName,
    String status,
    LocalDateTime createdAt,
    Integer slotCount,
    LocalDateTime earliestStart,
    LocalDateTime latestEnd,
    BigDecimal totalAmount,
    List<BookingSlotViewDto> slots
) {}
