package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.BookingDetailResponseDto;
import naitei.group5.workingspacebooking.dto.response.BookingHistoryResponseDto;
import naitei.group5.workingspacebooking.dto.response.PaymentResponseDto;
import naitei.group5.workingspacebooking.entity.Booking;
import naitei.group5.workingspacebooking.exception.custom.UserNotFoundException;
import naitei.group5.workingspacebooking.repository.BookingRepository;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public List<BookingHistoryResponseDto> getBookingHistory(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        List<Booking> bookings = bookingRepository.findByUser_IdOrderByCreatedAtDesc(userId);

        return bookings.stream().map(b -> {
            // map booking details
            var details = b.getBookingDetails().stream()
                    .map(d -> new BookingDetailResponseDto(
                            d.getId(),
                            d.getStartTime(),
                            d.getEndTime()
                    ))
                    .toList();

            // map payment (chỉ khi booked hoặc completed)
            PaymentResponseDto paymentDto = null;
            if ("booked".equalsIgnoreCase(b.getStatus().name()) ||
                    "completed".equalsIgnoreCase(b.getStatus().name())) {

                var payment = b.getPayments().stream().findFirst().orElse(null);
                if (payment != null) {
                    paymentDto = new PaymentResponseDto(
                            payment.getAmount(),
                            payment.getMethod(),
                            payment.getPaidTime()
                    );
                }
            }

            return new BookingHistoryResponseDto(
                    b.getId(),
                    b.getVenue().getName(),
                    b.getVenue().getLocation(),
                    b.getStatus().name(),
                    b.getCreatedAt(),
                    details,
                    paymentDto
            );
        }).toList();
    }
}
