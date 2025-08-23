package naitei.group5.workingspacebooking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record BookingDetailResponseDto(
        Integer id,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime startTime,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime endTime
) {}
