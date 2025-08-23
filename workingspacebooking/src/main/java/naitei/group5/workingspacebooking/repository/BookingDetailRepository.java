package naitei.group5.workingspacebooking.repository;

import naitei.group5.workingspacebooking.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {

    /**
     * Tìm các slot đã được đặt (busy slots) trong khoảng thời gian cụ thể của một venue.
     */
    @Query("""
        select bd
        from BookingDetail bd
            join bd.booking b
            join b.venue v
        where v.id = :venueId
          and bd.endTime > :start
          and bd.startTime < :end
        order by bd.startTime asc
    """)
    List<BookingDetail> findBusySlotsByVenueAndTimeRange(
            @Param("venueId") Integer venueId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Lọc các BookingDetail theo khoảng startTime.
     */
    List<BookingDetail> findByBooking_Venue_IdAndStartTimeBetween(
            Integer venueId,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * Lấy toàn bộ BookingDetail của một venue (fetch join để tránh N+1).
     */
    @Query("""
        select bd
        from BookingDetail bd
             join fetch bd.booking b
             join fetch b.venue v
        where v.id = :venueId
    """)
    List<BookingDetail> findByVenueId(@Param("venueId") Integer venueId);
}
