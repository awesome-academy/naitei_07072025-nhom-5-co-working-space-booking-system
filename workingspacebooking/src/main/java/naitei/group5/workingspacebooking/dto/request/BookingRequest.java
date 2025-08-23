package naitei.group5.workingspacebooking.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {
    private Integer venueId;
    private List<BookingSlot> slots;

    @Data
    public static class BookingSlot {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
