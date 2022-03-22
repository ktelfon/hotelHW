package lv.home.reservation.reservation.jpa.repository;

import lv.home.reservation.reservation.jpa.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByRoom_RoomId(Long roomId);

    @Query(value = "SELECT r FROM Reservation r WHERE NOT (r.checkInDate > :from OR r.checkOutDate < :to)")
    List<Reservation> findAllReservationsInBetweenPeriods(@Param("from") Instant from, @Param("to") Instant to);

}
