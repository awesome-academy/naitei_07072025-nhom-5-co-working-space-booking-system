package naitei.group5.workingspacebooking.dto.response;

import java.util.List;

public record VenueDetailResponseDto(
        Integer id,
        String name,
        String description,
        String image,
        Integer capacity,
        String location,
        Boolean verified,
        String venueStyleName,
        List<PriceDto> prices,
        List<BookingDto> bookings,
        List<TimeSlotDto> availableSlots,
        List<TimeSlotDto> busySlots
) {}
