package naitei.group5.workingspacebooking.controller.api.renter;

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
                                  @RequestBody BookingRequest req) {
        String accessToken = authHeader.replace("Bearer ", "");
        return bookingService.createBooking(accessToken, req);
    }

    @PutMapping("/{id}/cancel")
    public BookingResponse cancel(@RequestHeader("Authorization") String authHeader,
                                  @PathVariable Integer id) {
        String accessToken = authHeader.replace("Bearer ", "");
        return bookingService.cancelBooking(accessToken, id);
    }

}
