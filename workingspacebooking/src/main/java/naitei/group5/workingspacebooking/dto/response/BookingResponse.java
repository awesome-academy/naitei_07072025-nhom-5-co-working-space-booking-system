package naitei.group5.workingspacebooking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {
    private Integer bookingId;
    private Integer userId;
    private Integer venueId;
    private String venueName;
    private String status;
    private LocalDateTime createdAt;
    private Double totalPrice;
    private List<SlotResponse> slots;

    @Data
    @Builder
    public static class SlotResponse {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Double price;
    }
}
