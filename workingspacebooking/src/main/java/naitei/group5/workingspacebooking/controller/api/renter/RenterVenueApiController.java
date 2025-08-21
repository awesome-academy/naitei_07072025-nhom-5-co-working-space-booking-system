package naitei.group5.workingspacebooking.controller.api.renter;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRenterRequestDto;
import naitei.group5.workingspacebooking.dto.response.ApiResponse;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.service.VenueService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class RenterVenueApiController {

    private final VenueService venueService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VenueResponseDto>>> getVenues() {
        List<VenueResponseDto> venues = venueService.getVerifiedVenues();
        String message = messageSource.getMessage("venue.list.verified", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(ApiResponse.success(message, venues));
    }

    @PostMapping ("/filter")
    public ResponseEntity<ApiResponse<List<VenueResponseDto>>> filterVenues(@RequestBody FilterVenueRenterRequestDto req) {
        List<VenueResponseDto> venues = venueService.filterVenuesForRenter(req);
        String message = messageSource.getMessage("venue.list.filtered", null, LocaleContextHolder.getLocale());
        return ResponseEntity.ok(ApiResponse.success(message, venues));
    }
}
