package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.response.BookingHistoryResponseDto;
import java.util.List;

public interface BookingService {
    List<BookingHistoryResponseDto> getBookingHistory(Integer userId);
}
