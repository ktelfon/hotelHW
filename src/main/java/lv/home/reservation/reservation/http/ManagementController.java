package lv.home.reservation.reservation.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.home.reservation.reservation.ReservationService;
import lv.home.reservation.reservation.http.model.Pair;
import lv.home.reservation.reservation.http.model.RoomDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/management")
@AllArgsConstructor
public class ManagementController {

    private final ReservationService reservationService;

    @GetMapping("/reserved/{id}")
    public List<Pair<String, String>> getAllReservedDatesForRoom(@PathVariable("id") Long id) {
        log.info("Getting all reservation periods for room with id:{}", id);
        return reservationService.getAllReservedDatesForRoom(id);
    }

    @GetMapping("/availability/{from}/{to}")
    public List<RoomDto> getAvailability(@PathVariable("from") String from, @PathVariable("to") String to) {
        log.info("Getting all rooms that are reserved between: {} and {}", from, to);
        return reservationService.getAvailability(from, to);
    }
}
