package naitei.group5.workingspacebooking.dto.response;

import java.util.List;

public record VenueDetailRenterResponseDto(
        Integer id,
        String name,
        String description,
        Integer capacity,
        String location,
        Boolean verified,
        String image,
        String venueStyleName,
        List<PriceResponseDto> prices,
        List<BusySlotDto> busySlots
) {}
