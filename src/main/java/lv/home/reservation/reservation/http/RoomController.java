package lv.home.reservation.reservation.http;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.home.reservation.reservation.RoomService;
import lv.home.reservation.reservation.http.model.RoomDto;
import lv.home.reservation.reservation.jpa.exception.NoRoomFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/room")
@AllArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable("id") Long id) throws NoRoomFoundException {
        log.info("Getting room by id: {}", id);
        return Option.of(roomService.getRoomById(id))
                .map(r -> ResponseEntity.ok(RoomDto.builder()
                        .number(r.getNumber())
                        .price(r.getPrice())
                        .build()
                ))
                .getOrElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/")
    public ResponseEntity<?> save(@RequestBody RoomDto room) {
        log.info("Saving room");
        return Option.of(roomService.save(room))
                .map(r -> ResponseEntity.created(
                        UriComponentsBuilder
                                .fromUri(URI.create(""))
                                .pathSegment(r.getRoomId().toString())
                                .build().toUri()).build())
                .getOrElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody RoomDto room) {
        log.info("Updating room with id: {}", id);
        return Option.of(roomService.update(id, room))
                .map(r -> ResponseEntity.ok(RoomDto.builder()
                        .number(r.getNumber())
                        .price(r.getPrice())
                        .build()
                ))
                .getOrElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        log.info("Deleting room with id: {}", id);
        return roomService.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
