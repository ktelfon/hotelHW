package lv.home.reservation.reservation;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lv.home.reservation.reservation.http.model.Pair;
import lv.home.reservation.reservation.http.model.ReservationRequest;
import lv.home.reservation.reservation.http.model.RoomDto;
import lv.home.reservation.reservation.jpa.exception.NoRoomFoundException;
import lv.home.reservation.reservation.jpa.model.Reservation;
import lv.home.reservation.reservation.jpa.model.Room;
import lv.home.reservation.reservation.jpa.repository.ReservationRepository;
import lv.home.reservation.reservation.jpa.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public List<Pair<String, String>> getAllReservedDatesForRoom(Long id) {
        return Option.of(reservationRepository.findAllByRoom_RoomId(id))
                .map(reservations -> reservations.stream()
                        .map(reservation -> Pair.of(
                                LocalDate.ofInstant(reservation.getCheckInDate(), ZoneId.systemDefault()).toString(),
                                LocalDate.ofInstant(reservation.getCheckOutDate(), ZoneId.systemDefault()).toString()))
                        .toList())
                .getOrElse(Collections.emptyList());
    }

    public List<RoomDto> getAvailability(String from, String to) {
        Instant toInstant = stringToInstant(to);
        Instant fromInstant = stringToInstant(from);
        return Option.of(reservationRepository.findAllReservationsInBetweenPeriods(toInstant, fromInstant))
                .getOrElse(Collections.emptyList())
                .stream()
                .map(r -> Option.of(r.getRoom())
                        .map(room -> toRoomDto(room, true))
                        .getOrElse(RoomDto.builder().build())
                )
                .toList();
    }

    private RoomDto toRoomDto(Room room, boolean isVacant) {
        return RoomDto.builder()
                .isVacant(isVacant)
                .number(room.getNumber())
                .price(room.getPrice())
                .build();
    }

    private Instant stringToInstant(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }

    public boolean reserve(ReservationRequest request) throws NoRoomFoundException {
        Room room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new NoRoomFoundException());
        Instant checkInDate = stringToInstant(request.getFrom());
        Instant checkOutDate = stringToInstant(request.getTo());
        if (!room.getReservationList().isEmpty() && checkIfDatesAreTaken(room.getReservationList(), checkInDate, checkOutDate)) {
            return false;
        }
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(checkInDate);
        reservation.setCheckOutDate(checkOutDate);
        reservation.setRoom(room);
        reservation.setClientId(request.getClientId());
        room.getReservationList().add(reservation);
        roomRepository.save(room);
        return true;
    }

    private boolean checkIfDatesAreTaken(List<Reservation> reservationList, Instant checkInDate, Instant checkOutDate) {
        return reservationList.stream()
                .filter(reservation -> (checkInDate.isBefore(reservation.getCheckInDate()) || checkInDate.isAfter(reservation.getCheckOutDate()))
                        && (checkOutDate.isBefore(reservation.getCheckInDate()) || checkOutDate.isAfter(reservation.getCheckOutDate()))).count() > 1;
    }
}
