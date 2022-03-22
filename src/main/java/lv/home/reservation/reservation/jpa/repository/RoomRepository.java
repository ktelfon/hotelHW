package lv.home.reservation.reservation.jpa.repository;

import lv.home.reservation.reservation.jpa.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT f FROM Room f WHERE f.id NOT IN (SELECT r.room.roomId FROM Reservation r WHERE ((r.checkInDate NOT BETWEEN :from AND :to) AND (r.checkOutDate NOT BETWEEN :from AND :to)))")
    List<Room> getAllFreeRooms(@Param("from") Instant from, @Param("to") Instant to);
}
