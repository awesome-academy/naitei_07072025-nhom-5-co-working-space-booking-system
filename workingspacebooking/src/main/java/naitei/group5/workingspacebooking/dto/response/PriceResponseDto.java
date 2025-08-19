package naitei.group5.workingspacebooking.dto.response;

import naitei.group5.workingspacebooking.entity.enums.WeekDay;

import java.math.BigDecimal;
import java.time.LocalTime;

public record PriceResponseDto(
        Integer id,
        WeekDay dayOfWeek,
        LocalTime timeStart,
        LocalTime timeEnd,
        BigDecimal price
) {}
