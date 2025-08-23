package naitei.group5.workingspacebooking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
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
    private LocalDateTime canceledAt;
    private BigDecimal totalPrice;
    private List<SlotResponse> slots;

    @Data
    @Builder
    public static class SlotResponse {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private BigDecimal price;
    }
}
