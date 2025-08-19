package naitei.group5.workingspacebooking.dto.request;

public record FilterVenueRequestDto(
        Integer ownerId,
        String name,
        String location,
        Integer venueStyleId,
        String venueStyleName,
        Integer capacityMin,
        Integer capacityMax,
        Boolean verified) {}
