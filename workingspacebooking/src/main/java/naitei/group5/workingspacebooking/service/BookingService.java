package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.BookingRequest;
import naitei.group5.workingspacebooking.dto.response.BookingResponse;
import naitei.group5.workingspacebooking.dto.response.BookingHistoryResponseDto;
import java.util.List;

public interface BookingService {
    BookingResponse createBooking(String accessToken, BookingRequest req);
    BookingResponse cancelBooking(String accessToken, Integer bookingId);
    List<BookingHistoryResponseDto> getBookingHistory(Integer userId);
}
