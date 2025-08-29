package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.criteria.BookingAdminSearchCriteria;
import naitei.group5.workingspacebooking.dto.response.BookingAdminViewDto;
import naitei.group5.workingspacebooking.dto.response.BookingSlotViewDto;
import naitei.group5.workingspacebooking.entity.Booking;
import naitei.group5.workingspacebooking.entity.BookingDetail;
import naitei.group5.workingspacebooking.entity.enums.BookingStatus;
import naitei.group5.workingspacebooking.repository.BookingDetailRepository;
import naitei.group5.workingspacebooking.repository.BookingRepository;
import naitei.group5.workingspacebooking.service.AdminBookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBookingServiceImpl implements AdminBookingService {

    private final BookingRepository bookingRepository;
    private final BookingDetailRepository bookingDetailRepository;

    @Override
    public Page<BookingAdminViewDto> search(BookingAdminSearchCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        BookingStatus status = parseStatus(criteria.status());
        String normalizedUserEmail = normalizeUserEmail(criteria.userEmail());
        
        Page<Booking> pageData = bookingRepository.findAllAdmin(
            status,
            normalizedUserEmail,
            criteria.venueId(),
            pageable
        );
        
        if (pageData.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }
        
        List<Integer> bookingIds = pageData.getContent().stream()
            .map(Booking::getId)
            .toList();

        List<BookingDetail> details = bookingDetailRepository.findByBookingIdIn(bookingIds);

        Map<Integer, List<BookingDetail>> detailsByBookingId = details.stream()
            .collect(Collectors.groupingBy(detail -> detail.getBooking().getId()));

        List<BookingAdminViewDto> dtoList = pageData.getContent().stream()
            .map(booking -> mapToAdminViewDto(booking, detailsByBookingId.get(booking.getId())))
            .toList();
        
        return new PageImpl<>(dtoList, pageable, pageData.getTotalElements());
    }

    private BookingStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return BookingStatus.valueOf(status.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String normalizeUserEmail(String userEmail) {
        return (userEmail != null && !userEmail.isBlank()) ? userEmail.trim() : null;
    }

    private BookingAdminViewDto mapToAdminViewDto(Booking booking, List<BookingDetail> details) {
        List<BookingSlotViewDto> slotDtos = List.of();
        Integer slotCount = 0;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        if (details != null && !details.isEmpty()) {
            slotCount = details.size();
            
            earliestStart = details.stream()
                .map(BookingDetail::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);
            
            latestEnd = details.stream()
                .map(BookingDetail::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
            
            slotDtos = details.stream()
                .map(this::mapToSlotDto)
                .toList();
        }

        return new BookingAdminViewDto(
            booking.getId(),
            booking.getUser().getId(),
            booking.getUser().getName(),
            booking.getUser().getEmail(),
            booking.getVenue().getId(),
            booking.getVenue().getName(),
            booking.getStatus().name(),
            booking.getCreatedAt(),
            slotCount,
            earliestStart,
            latestEnd,
            booking.getTotalAmount(),
            slotDtos
        );
    }

    private BookingSlotViewDto mapToSlotDto(BookingDetail detail) {
        long durationMinutes = ChronoUnit.MINUTES.between(
            detail.getStartTime(), 
            detail.getEndTime()
        );
        
        return new BookingSlotViewDto(
            detail.getId(),
            detail.getStartTime(),
            detail.getEndTime(),
            durationMinutes
        );
    }
}
