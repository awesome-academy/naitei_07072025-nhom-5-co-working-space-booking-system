package naitei.group5.workingspacebooking.utils;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.VenueStyle;

public final class ConverterDto {

    
    public static Venue toVenueEntity(CreateVenueRequestDto req, User owner, VenueStyle venueStyle) {
        return Venue.builder()
                .name(req.name())
                .description(req.description())
                .capacity(req.capacity())
                .location(req.location())
                .image(req.image())
                .verified(false)
                .owner(owner)
                .venueStyle(venueStyle)
                .build();
    }

    
    public static VenueResponseDto toVenueResponseDto(Venue v) {
        Integer styleId = v.getVenueStyle() != null ? v.getVenueStyle().getId() : null;
        String styleName = v.getVenueStyle() != null ? v.getVenueStyle().getName() : null;

        return new VenueResponseDto(
                v.getId(),
                v.getName(),
                v.getImage(),
                v.getCapacity(),
                v.getLocation(),
                v.getVerified(),
                styleId,
                styleName
        );
    }

    
    public static void updateVenueFromDto(Venue venue, CreateVenueRequestDto req, VenueStyle venueStyle) {
        venue.setName(req.name());
        venue.setDescription(req.description());
        venue.setCapacity(req.capacity());
        venue.setLocation(req.location());
        venue.setImage(req.image());

        if (venueStyle != null) {
            venue.setVenueStyle(venueStyle);
        }
    }

    private ConverterDto() {
        throw new UnsupportedOperationException("Utility class không thể được khởi tạo");
    }
}
