package naitei.group5.workingspacebooking.service.common;
import naitei.group5.workingspacebooking.dto.response.TimeSlotResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
// TimeSlotCalculator.java
@Component
public class TimeSlotCalculator {
    public List<TimeSlotResponseDto> mergeBusy(LocalDateTime start, LocalDateTime end, List<TimeSlotResponseDto> busy) {
        var normalized = busy.stream()
                .map(s -> new TimeSlotResponseDto(
                        s.start().isBefore(start) ? start : s.start(),
                        s.end().isAfter(end) ? end : s.end()))
                .filter(s -> s.start().isBefore(s.end()))
                .sorted(Comparator.comparing(TimeSlotResponseDto::start))
                .toList();

        List<TimeSlotResponseDto> merged = new ArrayList<>();
        for (TimeSlotResponseDto slot : normalized) {
            if (merged.isEmpty() || slot.start().isAfter(merged.get(merged.size() - 1).end())) {
                merged.add(slot);
            } else {
                TimeSlotResponseDto last = merged.remove(merged.size() - 1);
                merged.add(new TimeSlotResponseDto(
                        last.start(),
                        last.end().isAfter(slot.end()) ? last.end() : slot.end()
                ));
            }
        }
        return merged;
    }

    public List<TimeSlotResponseDto> calcAvailable(LocalDateTime start, LocalDateTime end, List<TimeSlotResponseDto> mergedBusy) {
        List<TimeSlotResponseDto> free = new ArrayList<>();
        LocalDateTime cursor = start;
        for (TimeSlotResponseDto b : mergedBusy) {
            if (cursor.isBefore(b.start())) free.add(new TimeSlotResponseDto(cursor, b.start()));
            if (b.end().isAfter(cursor)) cursor = b.end();
        }
        if (cursor.isBefore(end)) free.add(new TimeSlotResponseDto(cursor, end));
        return free;
    }
}

