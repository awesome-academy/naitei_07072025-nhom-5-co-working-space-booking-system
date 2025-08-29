package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.Booking;
import naitei.group5.workingspacebooking.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // Lấy danh sách booking theo userId
    List<Booking> findByUser_IdOrderByCreatedAtDesc(Integer userId);

    // lấy danh sách booking theo owner
    List<Booking> findByVenue_Owner_IdOrderByCreatedAtDesc(Integer ownerId);

    // filter theo venueName + owner
    List<Booking> findByVenue_NameContainingIgnoreCaseAndVenue_Owner_IdOrderByCreatedAtDesc(String venueName, Integer ownerId);

    long count(); // tổng booking
    // Admin: Phân trang & filter (không fetch details)
    @Query("""
        SELECT b FROM Booking b
          JOIN FETCH b.user u
          JOIN FETCH b.venue v
        WHERE (:status IS NULL OR b.status = :status)
          AND (:userEmail IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :userEmail, '%')))
          AND (:venueId IS NULL OR v.id = :venueId)
        ORDER BY b.createdAt DESC
    """)
    Page<Booking> findAllAdmin(@Param("status") BookingStatus status,
                              @Param("userEmail") String userEmail,
                              @Param("venueId") Integer venueId,
                              Pageable pageable);
}
