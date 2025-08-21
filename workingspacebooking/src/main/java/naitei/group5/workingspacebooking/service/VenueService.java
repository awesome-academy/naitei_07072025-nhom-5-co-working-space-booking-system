package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.Venue;

import java.util.List;

public interface VenueService {

    // ==== Owner use cases ====
    VenueResponseDto createVenueRequest(Integer ownerId, CreateVenueRequestDto requestDto);

    List<Venue> getVenuesByOwner(Integer ownerId);

    List<VenueResponseDto> filterOwnerVenues(FilterVenueRequestDto req);

    // ==== Renter use cases ====
    List<VenueResponseDto> getVerifiedVenues();
    
    //Xem chi tiết venue của owner
    VenueDetailResponseDto getVenueDetailByOwner(Integer ownerId, Integer venueId);


}
