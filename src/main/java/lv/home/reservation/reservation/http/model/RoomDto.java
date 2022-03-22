package lv.home.reservation.reservation.http.model;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public class RoomDto {
    private Boolean isVacant;
    private Integer number;
    private Double price;
}
