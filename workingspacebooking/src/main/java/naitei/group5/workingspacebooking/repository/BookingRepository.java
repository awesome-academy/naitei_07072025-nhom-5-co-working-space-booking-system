package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Lấy danh sách booking theo userId
    List<Booking> findByUser_IdOrderByCreatedAtDesc(Integer userId);
    // lấy danh sách booking theo owner
    List<Booking> findByVenue_Owner_IdOrderByCreatedAtDesc(Integer ownerId);
}
