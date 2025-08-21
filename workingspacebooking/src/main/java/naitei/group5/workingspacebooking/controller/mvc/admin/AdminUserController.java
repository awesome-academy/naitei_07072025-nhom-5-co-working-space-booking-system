package naitei.group5.workingspacebooking.controller.mvc.admin;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller

@RequiredArgsConstructor
public class AdminUserController extends BaseAdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public String getUserList(
            @RequestParam(value = "role", required = false) UserRole role, // lấy role từ query param
            Model model,
            HttpServletRequest request) {

        List<UserResponse> users;

        if (role != null) {
            users = adminService.getUsersByRole(role);
        } else {
            users = adminService.getAllUsers();
        }

        model.addAttribute("users", users);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("currentUri", request.getRequestURI());

        return "admin/users/user-list";
    }
    @PostMapping("/users/{id}/approve-owner")
    public String approveOwner(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            adminService.approveOwner(id);
            redirectAttributes.addFlashAttribute("successMessage", "Phê duyệt thành công và đã gửi email!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
