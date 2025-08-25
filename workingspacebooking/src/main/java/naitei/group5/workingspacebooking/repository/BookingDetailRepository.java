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

    // Method
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
            @Param("end")   LocalDateTime end
    );

    // Method lọc khoảng startTime
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

    @Query("SELECT CASE WHEN COUNT(bd) > 0 THEN true ELSE false END " +
            "FROM BookingDetail bd " +
            "JOIN bd.booking b " +
            "WHERE b.venue.id = :venueId " +
            "AND b.status = 'booked' " +
            "AND (bd.startTime < :endTime AND bd.endTime > :startTime)")
    boolean existsConflict(@Param("venueId") Integer venueId,
                           @Param("startTime") LocalDateTime startTime,
                           @Param("endTime") LocalDateTime endTime);
}
