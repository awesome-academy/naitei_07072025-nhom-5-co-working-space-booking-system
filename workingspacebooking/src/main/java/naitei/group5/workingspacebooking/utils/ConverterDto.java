package naitei.group5.workingspacebooking.utils;

import naitei.group5.workingspacebooking.dto.request.CreateVenueRequestDto;
import naitei.group5.workingspacebooking.dto.response.PriceResponseDto;
import naitei.group5.workingspacebooking.dto.response.BookingDetailDto;
import naitei.group5.workingspacebooking.dto.response.BookingDto;
import naitei.group5.workingspacebooking.dto.response.PriceDto;
import naitei.group5.workingspacebooking.dto.response.TimeSlotDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.VenueResponseDto;
import naitei.group5.workingspacebooking.entity.*;
import java.util.List;
import java.util.Collections;


public final class ConverterDto {
//Venue
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
    Integer styleId  = (v.getVenueStyle() != null) ? v.getVenueStyle().getId()   : null;
    String  styleName= (v.getVenueStyle() != null) ? v.getVenueStyle().getName() : null;

    // ÉP KIỂU RÕ RÀNG => List<PriceResponseDto>, tránh List<?> do toán tử ?:
    List<PriceResponseDto> priceResponses =
            (v.getPrices() != null)
                    ? v.getPrices().stream()
                        .map(ConverterDto::toPriceResponseDto)
                        .toList()
                    : Collections.emptyList(); // hoặc List.<PriceResponseDto>of()

    return new VenueResponseDto(
            v.getId(),
            v.getName(),
            v.getDescription(),
            v.getCapacity(),
            v.getLocation(),
            v.getVerified(),
            v.getImage(),
            priceResponses,
            styleId,
            styleName
    );
}




    public static VenueDetailResponseDto toVenueDetailResponseDto(
        Venue v,
        List<TimeSlotDto> availableSlots,
        List<TimeSlotDto> busySlots
    ) {
        return new VenueDetailResponseDto(
                v.getId(),
                v.getName(),
                v.getDescription(),
                v.getImage(),
                v.getCapacity(),
                v.getLocation(),
                v.getVerified(),
                v.getVenueStyle() != null ? v.getVenueStyle().getName() : null,
                v.getPrices() != null ? v.getPrices().stream().map(ConverterDto::toPriceDto).toList() : List.of(),
                v.getBookings() != null ? v.getBookings().stream().map(ConverterDto::toBookingDto).toList() : List.of(),
                availableSlots,
                busySlots
        );
    }


    //Price
    public static PriceDto toPriceDto(Price p) {
        return new PriceDto(
                p.getDayOfWeek().name(),
                p.getTimeStart(),
                p.getTimeEnd(),
                p.getPrice().doubleValue()
        );
    }

    //Booking
    public static BookingDto toBookingDto(Booking b) {
        return new BookingDto(
                b.getId(),
                b.getUser() != null ? b.getUser().getId() : null,
                b.getStatus().name(),
                b.getCreatedAt(),
                b.getBookingDetails() != null ? b.getBookingDetails().stream().map(ConverterDto::toBookingDetailDto).toList() : List.of()
        );
    }

     public static BookingDetailDto toBookingDetailDto(BookingDetail bd) {
        return new BookingDetailDto(
                bd.getId(),
                bd.getStartTime(),
                bd.getEndTime()
        );
    }

    // Update Venue
    public static PriceResponseDto toPriceResponseDto(Price price) {
        return new PriceResponseDto(
                price.getId(),
                price.getDayOfWeek(),
                price.getTimeStart(),
                price.getTimeEnd(),
                price.getPrice()
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
