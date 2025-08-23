package naitei.group5.workingspacebooking.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {

    @NotNull(message = "Vui lòng chọn địa điểm (venueId).")
    private Integer venueId;

    @NotEmpty(message = "Danh sách khung giờ không được để trống.")
    @Size(min = 1, message = "Phải có ít nhất một khung giờ đặt chỗ.")
    private List<BookingSlot> slots;

    @Data
    public static class BookingSlot {
        @NotNull(message = "Vui lòng nhập thời gian bắt đầu.")
        @Future(message = "Thời gian bắt đầu phải ở tương lai.")
        private LocalDateTime startTime;

        @NotNull(message = "Vui lòng nhập thời gian kết thúc.")
        @Future(message = "Thời gian kết thúc phải ở tương lai.")
        private LocalDateTime endTime;
    }
}
