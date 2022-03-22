package lv.home.reservation.reservation.http;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.home.reservation.reservation.ReservationService;
import lv.home.reservation.reservation.http.model.ReservationRequest;
import lv.home.reservation.reservation.jpa.exception.NoRoomFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/reservation")
public class ReservationController {

    private ReservationService reservationService;

    @PostMapping("/")
    public ResponseEntity<?> reserveARoom(@Valid @RequestBody ReservationRequest request) throws NoRoomFoundException {
        return reservationService.reserve(request)
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }
}
