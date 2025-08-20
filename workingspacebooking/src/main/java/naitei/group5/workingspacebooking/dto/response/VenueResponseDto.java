package naitei.group5.workingspacebooking.dto.response;

import java.util.List;

public record VenueResponseDto(
        Integer id,
        String name,
        String description,
        Integer capacity,
        String location,
        Boolean verified,
        String image,
        List<PriceResponseDto> prices,
        Integer venueStyleId,
        String venueStyleName
) {}
