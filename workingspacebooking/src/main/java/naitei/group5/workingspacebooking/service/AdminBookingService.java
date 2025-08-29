package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.criteria.BookingAdminSearchCriteria;
import naitei.group5.workingspacebooking.dto.response.BookingAdminViewDto;
import org.springframework.data.domain.Page;

public interface AdminBookingService {
    Page<BookingAdminViewDto> search(BookingAdminSearchCriteria criteria, int page, int size);
}
