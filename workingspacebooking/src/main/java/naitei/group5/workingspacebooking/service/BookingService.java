package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.BookingRequest;
import naitei.group5.workingspacebooking.dto.response.BookingResponse;

public interface BookingService {
    BookingResponse createBooking(String accessToken, BookingRequest req);
}
