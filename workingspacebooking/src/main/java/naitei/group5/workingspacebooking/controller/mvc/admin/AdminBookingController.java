package naitei.group5.workingspacebooking.controller.mvc.admin;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.criteria.BookingAdminSearchCriteria;
import naitei.group5.workingspacebooking.dto.response.BookingAdminViewDto;
import naitei.group5.workingspacebooking.entity.enums.BookingStatus;
import naitei.group5.workingspacebooking.service.AdminBookingService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminBookingController extends BaseAdminController {

    private final AdminBookingService adminBookingService;

    @GetMapping("/bookings")
    public String listBookings(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "userEmail", required = false) String userEmail,
            @RequestParam(name = "venueId", required = false) Integer venueId,
            Model model) {
        
        int validatedPage = Math.max(1, page);
        int validatedSize = Math.min(Math.max(1, size), 100);
        
        BookingAdminSearchCriteria criteria = new BookingAdminSearchCriteria(
            userEmail,
            status,
            venueId
        );
        
        Page<BookingAdminViewDto> bookingsPage = adminBookingService.search(
            criteria, 
            validatedPage - 1, // Spring Data sử dụng 0-based index
            validatedSize
        );
        
        model.addAttribute("bookingsPage", bookingsPage);
        model.addAttribute("criteria", criteria);
        model.addAttribute("statuses", BookingStatus.values());
        model.addAttribute("currentPage", validatedPage);
        model.addAttribute("totalPages", bookingsPage.getTotalPages());
        model.addAttribute("hasContent", bookingsPage.hasContent());
        
        return "admin/bookings/list";
    }
}
