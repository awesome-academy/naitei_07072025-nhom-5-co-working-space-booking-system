package naitei.group5.workingspacebooking.dto.response;

import java.time.LocalDateTime;

public record BusySlotDto(
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status   // "booked" | "pending"
) {}
