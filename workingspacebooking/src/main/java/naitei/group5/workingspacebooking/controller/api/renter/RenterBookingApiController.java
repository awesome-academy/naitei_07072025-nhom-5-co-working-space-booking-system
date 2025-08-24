package naitei.group5.workingspacebooking.controller.api.renter;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.response.ApiResponse;
import naitei.group5.workingspacebooking.dto.response.BookingHistoryResponseDto;
import naitei.group5.workingspacebooking.service.BookingService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/renter/bookings")
@RequiredArgsConstructor
public class RenterBookingApiController {

    private final BookingService bookingService;
    private final MessageSource messageSource;

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<BookingHistoryResponseDto>>> getBookingHistory() {
        // 🔑 Lấy userId từ JWT (JwtAuthFilter đã set vào SecurityContext)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
        Integer userId = userDetails.getId();

        List<BookingHistoryResponseDto> history = bookingService.getBookingHistory(userId);

        String message = messageSource.getMessage(
                "booking.history.success", null, LocaleContextHolder.getLocale()
        );

        return ResponseEntity.ok(ApiResponse.success(message, history));
    }
}
