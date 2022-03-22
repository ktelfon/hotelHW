package lv.home.reservation.reservation.http

import lv.home.reservation.reservation.jpa.model.Room

trait RoomFixture {

    Room getRoom() {
        Room.builder()
                .number(1)
                .price(212.0)
                .build()
    }
}
