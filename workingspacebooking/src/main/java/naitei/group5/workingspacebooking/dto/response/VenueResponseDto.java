package naitei.group5.workingspacebooking.dto.response;

public record VenueResponseDto(
        Integer id,
        String name,
        String image,
        Integer capacity,
        String location,
        Boolean verified,
        Integer venueStyleId,
        String venueStyleName
) {}
