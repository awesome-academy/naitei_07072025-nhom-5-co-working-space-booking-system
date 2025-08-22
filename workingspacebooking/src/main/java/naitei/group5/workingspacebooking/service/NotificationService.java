package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.CreateSystemNotificationRequest;

public interface NotificationService {
    SendResult sendSystemNotification(CreateSystemNotificationRequest req);
}
