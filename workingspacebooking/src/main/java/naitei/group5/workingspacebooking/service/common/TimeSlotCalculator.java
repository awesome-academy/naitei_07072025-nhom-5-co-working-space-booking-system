package naitei.group5.workingspacebooking.service.common;
import naitei.group5.workingspacebooking.dto.response.TimeSlotDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
// TimeSlotCalculator.java
@Component
public class TimeSlotCalculator {
    public List<TimeSlotDto> mergeBusy(LocalDateTime start, LocalDateTime end, List<TimeSlotDto> busy) {
        var normalized = busy.stream()
                .map(s -> new TimeSlotDto(
                        s.start().isBefore(start) ? start : s.start(),
                        s.end().isAfter(end) ? end : s.end()))
                .filter(s -> s.start().isBefore(s.end()))
                .sorted(Comparator.comparing(TimeSlotDto::start))
                .toList();

        List<TimeSlotDto> merged = new ArrayList<>();
        for (TimeSlotDto slot : normalized) {
            if (merged.isEmpty() || slot.start().isAfter(merged.get(merged.size() - 1).end())) {
                merged.add(slot);
            } else {
                TimeSlotDto last = merged.remove(merged.size() - 1);
                merged.add(new TimeSlotDto(
                        last.start(),
                        last.end().isAfter(slot.end()) ? last.end() : slot.end()
                ));
            }
        }
        return merged;
    }

    public List<TimeSlotDto> calcAvailable(LocalDateTime start, LocalDateTime end, List<TimeSlotDto> mergedBusy) {
        List<TimeSlotDto> free = new ArrayList<>();
        LocalDateTime cursor = start;
        for (TimeSlotDto b : mergedBusy) {
            if (cursor.isBefore(b.start())) free.add(new TimeSlotDto(cursor, b.start()));
            if (b.end().isAfter(cursor)) cursor = b.end();
        }
        if (cursor.isBefore(end)) free.add(new TimeSlotDto(cursor, end));
        return free;
    }
}

