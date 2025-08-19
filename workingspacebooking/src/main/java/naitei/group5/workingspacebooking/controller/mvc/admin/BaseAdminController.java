package naitei.group5.workingspacebooking.controller.mvc.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;

@PreAuthorize("hasRole('admin')")
@RequestMapping("/admin")
public abstract class BaseAdminController {
    // Base controller cho tất cả admin controllers
    // Áp dụng quy tắc bảo mật chung: yêu cầu role admin
}
