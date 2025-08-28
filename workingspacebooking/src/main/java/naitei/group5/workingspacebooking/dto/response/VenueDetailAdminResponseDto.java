package naitei.group5.workingspacebooking.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record VenueDetailAdminResponseDto(
        Integer id,
        String name,
        String description,
        Integer capacity,
        String location,
        String image,
        String venueStyleName,
        Boolean verified,
        OwnerSummary owner,
        List<PriceResponseDto> prices,
        List<BusySlotDto> busySlots,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record OwnerSummary(
            Integer id,
            String name,
            String email
    ) {}
}
