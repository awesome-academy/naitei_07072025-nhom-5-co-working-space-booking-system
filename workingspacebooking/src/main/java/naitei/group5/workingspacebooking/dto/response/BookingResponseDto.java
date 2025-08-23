package naitei.group5.workingspacebooking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDto(
        Integer bookingId,
        Integer userId,
        String status,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime createdAt,
        List<BookingDetailResponseDto> details
) {}
