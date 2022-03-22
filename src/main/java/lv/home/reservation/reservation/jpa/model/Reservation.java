package lv.home.reservation.reservation.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @ToString.Exclude
    Room room;
    Instant checkInDate;
    Instant checkOutDate;
    Long clientId;
}
