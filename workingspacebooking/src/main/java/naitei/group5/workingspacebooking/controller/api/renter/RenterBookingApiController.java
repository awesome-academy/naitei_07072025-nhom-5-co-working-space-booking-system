package naitei.group5.workingspacebooking.controller.api.renter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import naitei.group5.workingspacebooking.dto.request.BookingRequest;
import naitei.group5.workingspacebooking.dto.response.BookingResponse;
import naitei.group5.workingspacebooking.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor

public class RenterBookingApiController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse create(@RequestHeader("Authorization") String authHeader,
                                  @Valid @RequestBody BookingRequest req) {
        String accessToken = authHeader.replace("Bearer ", "");
        return bookingService.createBooking(accessToken, req);
    }
}
