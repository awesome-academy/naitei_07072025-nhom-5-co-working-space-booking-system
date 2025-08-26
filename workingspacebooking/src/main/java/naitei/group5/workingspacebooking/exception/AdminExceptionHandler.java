package naitei.group5.workingspacebooking.exception;

import naitei.group5.workingspacebooking.exception.custom.VenueNotFoundException;
import naitei.group5.workingspacebooking.exception.custom.VenueVerificationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

// Exception handler cho các trang Admin MVC
@ControllerAdvice(basePackages = "naitei.group5.workingspacebooking.controller.mvc.admin")
@Component
public class AdminExceptionHandler {

    private final MessageSource messageSource;

    public AdminExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({VenueNotFoundException.class, ResourceNotFoundException.class})
    public String handleResourceNotFoundException(Exception ex, 
                                                  RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request) {
        String message = "Resource not found";
        
        if (ex instanceof VenueNotFoundException) {
            VenueNotFoundException venueEx = (VenueNotFoundException) ex;
            try {
                message = messageSource.getMessage("venue.error.notfound", 
                        new Object[]{venueEx.getVenueId()}, 
                        LocaleContextHolder.getLocale());
            } catch (Exception e) {
                message = ex.getMessage();
            }
        } else {
            message = ex.getMessage();
        }
        
        redirectAttributes.addFlashAttribute("errorMessage", message);
        
        return determineRedirectPath(request);
    }

    @ExceptionHandler(VenueVerificationException.class)
    public String handleVenueVerificationException(VenueVerificationException ex, 
                                                  RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request) {
        String messageKey = "venue.error." + ex.getOperation();
        String message;
        
        try {
            message = messageSource.getMessage(messageKey, 
                                             new Object[]{ex.getVenueId()}, 
                                             LocaleContextHolder.getLocale());
        } catch (Exception e) {
            try {
                message = messageSource.getMessage("venue.error.generic", 
                                                 null, 
                                                 LocaleContextHolder.getLocale());
            } catch (Exception e2) {
                message = ex.getMessage();
            }
        }
        
        redirectAttributes.addFlashAttribute("errorMessage", message);
        
        return determineRedirectPath(request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, 
                                             RedirectAttributes redirectAttributes,
                                             HttpServletRequest request) {
        String messageKey = ex.getMessage();
        String message;
        
        try {
            message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            message = ex.getMessage();
        }
        
        redirectAttributes.addFlashAttribute("errorMessage", message);
        
        return determineRedirectPath(request);
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, 
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        String message;
        
        try {
            message = messageSource.getMessage("error.general", 
                    null, 
                    "An unexpected error occurred", 
                    LocaleContextHolder.getLocale());
        } catch (Exception e) {
            message = "An unexpected error occurred: " + ex.getMessage();
        }
        
        redirectAttributes.addFlashAttribute("errorMessage", message);
        
        return determineRedirectPath(request);
    }

    private String determineRedirectPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        
        if (referer != null) {
            if (referer.contains("/admin/venues/verification")) {
                return "redirect:/admin/venues/verification";
            }
            if (referer.contains("/admin/users")) {
                return "redirect:/admin/users";
            }
            if (referer.contains("/admin/venues")) {
                return "redirect:/admin/venues";
            }
        }
        
        // Default fallback
        return "redirect:/admin/dashboard";
    }
}
