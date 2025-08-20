package naitei.group5.workingspacebooking.controller.api.renter;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.ApiResponse;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.service.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class RenterVenueApiController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VenueResponseDto>>> getVenues() {
        List<VenueResponseDto> venues = venueService.getVerifiedVenues();
        return ResponseEntity.ok(ApiResponse.success("Verified venue list fetched successfully", venues));
    }
}
