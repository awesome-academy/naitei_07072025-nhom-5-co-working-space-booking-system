package naitei.group5.workingspacebooking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TimeSlotDto(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime start,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime end
) {}
