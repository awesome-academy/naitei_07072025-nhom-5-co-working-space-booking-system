package naitei.group5.workingspacebooking.controller.mvc.admin;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.AdminVenueViewDto;
import naitei.group5.workingspacebooking.service.VenueService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AdminVenueController extends BaseAdminController {

    private final VenueService venueService;
    private final MessageSource messageSource;

    @GetMapping("/venues")
    public String listVenues(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            Model model) {

        // Phân trang
        org.springframework.data.domain.Page<AdminVenueViewDto> venuesPage = 
            venueService.adminListVenues(name, status, sort, page, size);
        
        model.addAttribute("venues", venuesPage);
        model.addAttribute("currentName", name);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        
        model.addAttribute("totalPages", venuesPage.getTotalPages());
        model.addAttribute("totalElements", venuesPage.getTotalElements());
        model.addAttribute("isShowAll", size == -1);
        
        return "admin/venues/list";
    }

    @GetMapping("/venues/verification")
    public String venueVerificationPage(
            @RequestParam(required = false, defaultValue = "unverified") String status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            Model model) {
        
        // Phân trang
        org.springframework.data.domain.Page<AdminVenueViewDto> venuesPage = 
            venueService.adminListVenues(null, status, "name_asc", page, size);
        
        model.addAttribute("venues", venuesPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSize", size);
        
        model.addAttribute("totalPages", venuesPage.getTotalPages());
        model.addAttribute("totalElements", venuesPage.getTotalElements());
        model.addAttribute("isShowAll", size == -1);
        
        return "admin/venues/verification";
    }

    @PatchMapping("/venues/{id}/approve")
    public String approveVenue(@PathVariable Integer id,
                              @RequestParam(required = false, defaultValue = "unverified") String status,
                              @RequestParam(required = false, defaultValue = "0") int page,
                              @RequestParam(required = false, defaultValue = "20") int size,
                              RedirectAttributes redirectAttributes) {
        
        venueService.approveVenue(id);
        String message = messageSource.getMessage("venue.approve.success", 
                null, 
                LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        
        return "redirect:/admin/venues/verification?status=" + status + "&page=" + page + "&size=" + size;
    }

    @PatchMapping("/venues/{id}/unverify")
    public String unverifyVenue(@PathVariable Integer id,
                               @RequestParam(required = false, defaultValue = "unverified") String status,
                               @RequestParam(required = false, defaultValue = "0") int page,
                               @RequestParam(required = false, defaultValue = "20") int size,
                               RedirectAttributes redirectAttributes) {
        
        venueService.unverifyVenue(id);
        String message = messageSource.getMessage("venue.unverify.success", 
                null, 
                LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        
        return "redirect:/admin/venues/verification?status=" + status + "&page=" + page + "&size=" + size;
    }
}
