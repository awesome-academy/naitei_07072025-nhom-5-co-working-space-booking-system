package naitei.group5.workingspacebooking.controller.mvc.admin;

import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.service.AdminService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminDashboardController extends BaseAdminController {

    private final AdminService adminService;

    public AdminDashboardController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request, 
                           @AuthenticationPrincipal JwtUserDetails userDetails) {
        String email = userDetails.getUsername();
        
        UserResponse user = adminService.getUserByEmail(email);

        model.addAttribute("userName", user.getName());
        model.addAttribute("currentUri", request.getRequestURI());

        return "admin/dashboard";
    }
}
