package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.Notification;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    long countByUser_Role(UserRole role); // số notification admin đã gửi
}

