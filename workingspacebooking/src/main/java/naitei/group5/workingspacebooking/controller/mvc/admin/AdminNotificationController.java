package naitei.group5.workingspacebooking.controller.mvc.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.request.CreateSystemNotificationRequest;
import naitei.group5.workingspacebooking.entity.enums.NotificationType;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.service.NotificationService;
import naitei.group5.workingspacebooking.service.SendResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AdminNotificationController extends BaseAdminController {

    private final NotificationService notificationService;

    @GetMapping("/notifications/new")
    public String showNotificationForm(Model model) {
        model.addAttribute("request", new CreateSystemNotificationRequest("", "", null, null));
        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("notificationTypes", NotificationType.values());
        
        return "admin/notifications/new";
    }

    @PostMapping("/notifications")
    public String sendNotification(
            @Valid @ModelAttribute("request") CreateSystemNotificationRequest request,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal JwtUserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userRoles", UserRole.values());
            model.addAttribute("notificationTypes", NotificationType.values());
            return "admin/notifications/new";
        }

        SendResult result = notificationService.sendSystemNotification(request);
        model.addAttribute("result", result);
        
        return "admin/notifications/result";
    }
}
