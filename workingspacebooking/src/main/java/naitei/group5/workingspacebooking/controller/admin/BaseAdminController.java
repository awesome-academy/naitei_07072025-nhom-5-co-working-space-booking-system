package naitei.group5.workingspacebooking.controller.admin;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('admin')")
public abstract class BaseAdminController {

}
