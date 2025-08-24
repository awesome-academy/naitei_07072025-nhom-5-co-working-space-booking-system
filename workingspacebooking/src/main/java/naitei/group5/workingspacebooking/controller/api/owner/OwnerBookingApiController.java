package naitei.group5.workingspacebooking.controller.api.owner;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.response.ApiResponse;
import naitei.group5.workingspacebooking.dto.response.BookingHistoryResponseDto;
import naitei.group5.workingspacebooking.service.BookingService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/owner/bookings")
@RequiredArgsConstructor
public class OwnerBookingApiController {

    private final BookingService bookingService;
    private final MessageSource messageSource;

    // Xem tất cả booking tại các venue mà Owner sở hữu, có thể filter theo venueName
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingHistoryResponseDto>>> getBookingsAtMyVenues(
            @AuthenticationPrincipal JwtUserDetails userDetails,
            @RequestParam(required = false) String venueName
    ) {
        Integer ownerId = userDetails.getId();

        List<BookingHistoryResponseDto> bookings;
        if (venueName != null && !venueName.isBlank()) {
            bookings = bookingService.getBookingsByOwnerAndVenueName(ownerId, venueName);
        } else {
            bookings = bookingService.getBookingsByOwner(ownerId);
        }

        String message = messageSource.getMessage(
                "owner.bookings.success", null, LocaleContextHolder.getLocale()
        );

        return ResponseEntity.ok(ApiResponse.success(message, bookings));
    }
}
