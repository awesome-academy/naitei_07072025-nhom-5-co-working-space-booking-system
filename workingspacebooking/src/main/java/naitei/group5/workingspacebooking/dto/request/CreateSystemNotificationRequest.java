package naitei.group5.workingspacebooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import naitei.group5.workingspacebooking.entity.enums.NotificationType;
import naitei.group5.workingspacebooking.entity.enums.UserRole;

import java.util.Set;

public record CreateSystemNotificationRequest(
    @NotBlank(message = "{notification.title.required}")
    String title,
    
    @NotBlank(message = "{notification.content.required}")
    String content,
    
    @NotNull(message = "{notification.type.required}")
    NotificationType type,
    
    @NotEmpty(message = "{notification.target.roles.required}")
    Set<UserRole> targetRoles
) {}
