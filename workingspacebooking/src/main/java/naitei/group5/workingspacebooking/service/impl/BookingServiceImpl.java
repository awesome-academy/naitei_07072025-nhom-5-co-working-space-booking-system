package naitei.group5.workingspacebooking.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.entity.enums.BookingStatus;
import naitei.group5.workingspacebooking.entity.enums.WeekDay;
import org.springframework.stereotype.Service;

import naitei.group5.workingspacebooking.dto.request.BookingRequest;
import naitei.group5.workingspacebooking.dto.response.BookingResponse;
import naitei.group5.workingspacebooking.entity.Booking;
import naitei.group5.workingspacebooking.entity.BookingDetail;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.Venue;
import naitei.group5.workingspacebooking.repository.BookingDetailRepository;
import naitei.group5.workingspacebooking.repository.BookingRepository;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.service.BookingService;
import naitei.group5.workingspacebooking.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra venue
        Venue venue = venueRepo.findById(req.getVenueId())
                .orElseThrow(() -> new RuntimeException("Venue not found"));
        if (venue.getVerified() == null || !venue.getVerified()) {
            throw new RuntimeException("Venue not verified");
        }

        // Tạo booking cha
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setVenue(venue);
        booking.setStatus(BookingStatus.booked);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setTotalAmount(0.0);

        final Booking savedBooking = bookingRepo.save(booking);

        List<BookingResponse.SlotResponse> slots = req.getSlots().stream().map(slot -> {
            if (slot.getEndTime().isBefore(slot.getStartTime())) {
                throw new RuntimeException("Invalid time range");
            }

            boolean conflict = bookingDetailRepo.existsConflict(
                    venue.getId(), slot.getStartTime(), slot.getEndTime()
            );
            if (conflict) {
                throw new RuntimeException("Venue already booked at " + slot.getStartTime());
            }

            WeekDay dayOfWeek = WeekDay.valueOf(slot.getStartTime().getDayOfWeek().name().toLowerCase());

            var priceRule = venue.getPrices().stream()
                    .filter(p -> p.getDayOfWeek() == dayOfWeek
                            && !slot.getStartTime().toLocalTime().isBefore(p.getTimeStart())
                            && !slot.getEndTime().toLocalTime().isAfter(p.getTimeEnd()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No price rule found for this slot"));

            long hours = java.time.Duration.between(slot.getStartTime(), slot.getEndTime()).toHours();
            if (hours <= 0) throw new RuntimeException("Invalid duration");

            double slotPrice = priceRule.getPrice().doubleValue() * hours;

            BookingDetail detail = new BookingDetail();
            detail.setBooking(savedBooking);
            detail.setStartTime(slot.getStartTime());
            detail.setEndTime(slot.getEndTime());
            bookingDetailRepo.save(detail);

            return BookingResponse.SlotResponse.builder()
                    .startTime(slot.getStartTime())
                    .endTime(slot.getEndTime())
                    .price(slotPrice)
                    .build();
        }).collect(Collectors.toList());

        double totalPrice = slots.stream().mapToDouble(BookingResponse.SlotResponse::getPrice).sum();

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

    @Override
    @Transactional
    public BookingResponse cancelBooking(String accessToken, Integer bookingId) {
        // Lấy user từ token
        var claims = jwtUtils.parse(accessToken).getBody();
        String email = claims.getSubject();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm booking
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra quyền: booking phải thuộc về user
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You do not have permission to cancel this booking");
        }

        // Chỉ hủy khi status = booked
        if (booking.getStatus() != BookingStatus.booked) {
            throw new RuntimeException("Booking cannot be canceled");
        }

        // Cập nhật trạng thái và thời gian hủy
        booking.setStatus(BookingStatus.canceled);
        booking.setCanceledAt(LocalDateTime.now());
        bookingRepo.save(booking);

        // Trả về response
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .userId(user.getId())
                .venueId(booking.getVenue().getId())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .canceledAt(booking.getCanceledAt())
                .totalPrice(booking.getTotalAmount())
                .slots(booking.getBookingDetails().stream().map(d ->
                        BookingResponse.SlotResponse.builder()
                                .startTime(d.getStartTime())
                                .endTime(d.getEndTime())
                                .price(0.0) // có thể tính lại nếu cần
                                .build()
                ).toList())
                .build();
    }

}
