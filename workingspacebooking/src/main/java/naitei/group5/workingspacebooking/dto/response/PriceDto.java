package naitei.group5.workingspacebooking.dto.response;

import java.time.LocalTime;

public record PriceDto(
        String dayOfWeek,
        LocalTime start,
        LocalTime end,
        Double price
) {}
