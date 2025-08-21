package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;

import java.time.LocalDateTime;

public interface VenueDetailService {
    VenueDetailResponseDto getVenueDetail(Integer venueId, LocalDateTime start, LocalDateTime end);
}
