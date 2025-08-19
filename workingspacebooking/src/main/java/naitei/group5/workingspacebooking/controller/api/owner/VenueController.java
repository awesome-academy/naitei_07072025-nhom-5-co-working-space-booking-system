package naitei.group5.workingspacebooking.controller.api.owner;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.service.VenueService;
import naitei.group5.workingspacebooking.utils.ConverterDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated; 

import java.util.List;


@RestController
@RequestMapping("/api/owner/venues")
@RequiredArgsConstructor
@Validated
public class VenueController {

    private final VenueService venueService;

    // GET /api/owner/venues?ownerId=123  (list venue của owner)
    @GetMapping("/{ownerId}")
    public List<VenueResponseDto> listMyVenues(@PathVariable Integer ownerId) {
        return venueService.getVenuesByOwner(ownerId)
                .stream()
                .map(ConverterDto::toVenueResponseDto)
                .toList();
    }

    // POST /api/owner/venues  (owner tạo venue)
    @PostMapping
    public ResponseEntity<VenueResponseDto> createVenueRequest(
            @Valid @RequestBody CreateVenueRequestDto requestDto) {

        VenueResponseDto responseDto =
                venueService.createVenueRequest(requestDto.ownerId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // POST /api/owner/venues/filter  (lọc theo tiêu chí)
    @PostMapping("/filter")
    public List<VenueResponseDto> filterVenues(@RequestBody FilterVenueRequestDto req) {
        return venueService.filterOwnerVenues(req);
    }
}
