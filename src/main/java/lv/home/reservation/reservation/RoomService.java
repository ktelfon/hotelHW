package lv.home.reservation.reservation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lv.home.reservation.reservation.http.model.RoomDto;
import lv.home.reservation.reservation.jpa.exception.NoRoomFoundException;
import lv.home.reservation.reservation.jpa.model.Room;
import lv.home.reservation.reservation.jpa.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Room getRoomById(Long id) throws NoRoomFoundException {
        return roomRepository.findById(id).orElseThrow(() -> new NoRoomFoundException());
    }

    public Room save(RoomDto roomDto) {
        return roomRepository.save(Room.builder()
                .number(roomDto.getNumber())
                .price(roomDto.getPrice())
                .build());
    }

    public Room update(Long id, RoomDto roomDto) {
        if (!roomRepository.existsById(id)) {
            log.info("No room with id:{} found", id);
            return Room.builder().build();
        }
        return roomRepository.save(Room.builder()
                .roomId(id)
                .number(roomDto.getNumber())
                .price(roomDto.getPrice())
                .build());
    }

    public Boolean delete(Long id) {
        if (!roomRepository.existsById(id)) {
            log.info("No room with id:{} found", id);
            return false;
        }
        roomRepository.deleteById(id);
        return true;
    }
}
