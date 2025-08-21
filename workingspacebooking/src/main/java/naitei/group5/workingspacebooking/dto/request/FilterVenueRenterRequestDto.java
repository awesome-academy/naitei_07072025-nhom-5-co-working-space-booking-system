package naitei.group5.workingspacebooking.dto.request;

import java.time.LocalTime;
import java.util.List;
import naitei.group5.workingspacebooking.entity.enums.WeekDay;

public record FilterVenueRenterRequestDto(
        String name,
        String location,
        String venueStyleName,
        Integer venueStyleId,
        Integer capacityMin,
        Integer capacityMax,
        LocalTime startTime,
        LocalTime endTime,
        List<WeekDay> weekDays
) {}
