package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.request.FilterVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.Venue;

import java.util.List;

public interface VenueService {

    // Owner tạo yêu cầu/tạo venue
    VenueResponseDto createVenueRequest(Integer ownerId, CreateVenueRequestDto requestDto);

    // Lấy danh sách venue của 1 owner
    List<Venue> getVenuesByOwner(Integer ownerId);

    // Lọc danh sách venue của owner theo tiêu chí
    List<VenueResponseDto> filterOwnerVenues(FilterVenueRequestDto req);
}
