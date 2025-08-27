package naitei.group5.workingspacebooking.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.request.BookingRequest;
import naitei.group5.workingspacebooking.dto.response.BookingResponse;
import naitei.group5.workingspacebooking.entity.Booking;
import naitei.group5.workingspacebooking.entity.BookingDetail;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.entity.enums.BookingStatus;
import naitei.group5.workingspacebooking.entity.enums.WeekDay;
import naitei.group5.workingspacebooking.exception.custom.*;
import naitei.group5.workingspacebooking.repository.BookingDetailRepository;
import naitei.group5.workingspacebooking.repository.BookingRepository;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.service.BookingService;
import naitei.group5.workingspacebooking.utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final BookingDetailRepository bookingDetailRepo;
    private final VenueRepository venueRepo;
    private final UserRepository userRepo;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public BookingResponse createBooking(String accessToken, BookingRequest req) {
        // Lấy user từ access token
        var claims = jwtUtils.parse(accessToken).getBody();
        String email = claims.getSubject();
        User user = userRepo.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        // Kiểm tra venue
        Venue venue = venueRepo.findById(req.getVenueId())
                .orElseThrow(VenueNotFoundException::new);
        if (venue.getVerified() == null || !venue.getVerified()) {
            throw new VenueNotVerifiedException();
        }

        // Tạo booking cha
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setVenue(venue);
        booking.setStatus(BookingStatus.booked);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setTotalAmount(BigDecimal.ZERO);

        final Booking savedBooking = bookingRepo.save(booking);

        // Xử lý các slot
        List<BookingResponse.SlotResponse> slots = req.getSlots().stream()
                .map(slot -> processSlot(venue, savedBooking, slot))
                .collect(Collectors.toList());

        // Tính tổng tiền (dùng BigDecimal trực tiếp)
        BigDecimal totalPrice = slots.stream()
                .map(BookingResponse.SlotResponse::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        savedBooking.setTotalAmount(totalPrice);
        bookingRepo.save(savedBooking);

        // Trả về response
        return BookingResponse.builder()
                .bookingId(savedBooking.getId())
                .userId(user.getId())
                .venueId(venue.getId())
                .status(savedBooking.getStatus().name())
                .createdAt(savedBooking.getCreatedAt())
                .totalPrice(totalPrice)
                .slots(slots)
                .build();
    }

    private BookingResponse.SlotResponse processSlot(Venue venue, Booking booking, BookingRequest.BookingSlot slot) {
        if (slot.getEndTime().isBefore(slot.getStartTime())) {
            throw new InvalidTimeRangeException();
        }

        boolean conflict = bookingDetailRepo.existsConflict(
                venue.getId(), slot.getStartTime(), slot.getEndTime()
        );
        if (conflict) {
            throw new BookingConflictException();
        }

        WeekDay dayOfWeek = WeekDay.valueOf(slot.getStartTime().getDayOfWeek().name().toLowerCase());

        var priceRule = venue.getPrices().stream()
                .filter(p -> p.getDayOfWeek() == dayOfWeek
                        && !slot.getStartTime().toLocalTime().isBefore(p.getTimeStart())
                        && !slot.getEndTime().toLocalTime().isAfter(p.getTimeEnd()))
                .findFirst()
                .orElseThrow(PriceRuleNotFoundException::new);

        long hours = Duration.between(slot.getStartTime(), slot.getEndTime()).toHours();
        if (hours <= 0) {
            throw new InvalidDurationException();
        }


        BigDecimal slotPrice = priceRule.getPrice().multiply(BigDecimal.valueOf(hours));

        BookingDetail detail = new BookingDetail();
        detail.setBooking(booking);
        detail.setStartTime(slot.getStartTime());
        detail.setEndTime(slot.getEndTime());
        bookingDetailRepo.save(detail);

        return BookingResponse.SlotResponse.builder()
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .price(slotPrice)
                .build();
    }
}
