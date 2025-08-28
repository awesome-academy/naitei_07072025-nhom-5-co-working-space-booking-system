package naitei.group5.workingspacebooking.dto.response;

import java.time.LocalDateTime;

public record BookingSlotViewDto(
    Integer slotId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Long durationMinutes
) {}
