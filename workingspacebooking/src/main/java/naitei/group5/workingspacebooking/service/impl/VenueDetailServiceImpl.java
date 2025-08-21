package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.TimeSlotDto;
import naitei.group5.workingspacebooking.dto.response.VenueDetailResponseDto;
import naitei.group5.workingspacebooking.service.common.TimeSlotCalculator;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.exception.ResourceNotFoundException;
import naitei.group5.workingspacebooking.repository.BookingDetailRepository;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.service.VenueDetailService;
import naitei.group5.workingspacebooking.utils.ConverterDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueDetailServiceImpl implements VenueDetailService {

    private final VenueRepository venueRepository;
    private final BookingDetailRepository bookingDetailRepository;

    private final TimeSlotCalculator timeSlotCalculator;

    
    @Override
    public VenueDetailResponseDto getVenueDetail(Integer venueId, LocalDateTime start, LocalDateTime end) {
        // 1. Lấy venue kèm all details
        Venue venue = venueRepository.findByIdWithDetails(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + venueId));

        List<TimeSlotDto> busySlots = bookingDetailRepository
        .findBusySlotsByVenueAndTimeRange(venueId, start, end)
        .stream()
        .map(bd -> new TimeSlotDto(bd.getStartTime(), bd.getEndTime()))
        .toList();

        // merge busy slots trước
        List<TimeSlotDto> mergedBusy = timeSlotCalculator.mergeBusy(start, end, busySlots);

        // tính available slots từ busy
        List<TimeSlotDto> availableSlots = timeSlotCalculator.calcAvailable(start, end, mergedBusy);

        // truyền đủ 2 tham số
        return ConverterDto.toVenueDetailResponseDto(venue, availableSlots, mergedBusy);
    }
}
