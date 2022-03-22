package lv.home.reservation.reservation.http

import lv.home.reservation.reservation.http.model.ReservationRequest
import lv.home.reservation.reservation.jpa.model.Reservation
import lv.home.reservation.reservation.jpa.model.Room
import lv.home.reservation.reservation.jpa.repository.ReservationRepository
import lv.home.reservation.reservation.jpa.repository.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.util.concurrent.PollingConditions

import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

class ReservationControllerTest extends WebIntegrationSpec implements RoomFixture {

    @Autowired
    private RoomRepository roomRepository

    @Autowired
    private ReservationRepository reservationRepository

    def "should reserve a room"() {
        given:
        def room = getRoom()
        def roomNumber = room.number
        def roomId = roomRepository.save(room).roomId
        def clientId = 123l
        def reservationRequest = ReservationRequest.builder()
                .clientId(clientId)
                .roomId(roomId)
                .from("01-01-2001")
                .to("03-01-2001")
                .build()

        String requestBody = jsonInString(reservationRequest)

        def request = post("/reservation/", requestBody)
        when:
        def resp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        then:
        resp.get().statusCode() == 200
        await(2) {
            reservationRepository.findAllByRoom_RoomId(roomId)[0].clientId == clientId
            reservationRepository.findAllByRoom_RoomId(roomId)[0].room.number == roomNumber
        }
    }

    private HttpRequest post(String path, String requestBody) {
        HttpRequest.newBuilder(URI.create(baseUrl + path))
                .headers("Content-Type", APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build()
    }

    def "should not reserve a room date is occupied"() {
        given:
        def room = getRoom()
        def clientId = 123
        room = roomRepository.save(room)
        def roomId = room.roomId
        def reservationRequest = ReservationRequest.builder()
                .clientId(clientId)
                .roomId(roomId)
                .from("01-01-2001")
                .to("03-01-2001")
                .build()

        def reservation = new Reservation()
        reservation.clientId = clientId
        reservation.room = room

        reservation.checkInDate = stringToInstant("02-01-2001")
        reservation.checkOutDate = stringToInstant("03-01-2001")
        room.reservationList.add(reservation)
        roomRepository.save(room)

        String requestBody = jsonInString(reservationRequest)
        def request = post("/reservation/", requestBody)

        when:
        def resp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        then:
        resp.get().statusCode() == 200
        await(2) {
            reservationRepository.findAll().size() == 1
        }
    }

    def "should retrieve all free rooms in a date frame"() {
        given:
        def freeRoom = getRoom()
        def takenRoom = getRoom()
        takenRoom.number = 21

        freeRoom = roomRepository.save(freeRoom)
        takenRoom = roomRepository.save(takenRoom)

        def reservation = new Reservation()
        reservation.room = takenRoom

        reservation.checkInDate = stringToInstant("02-01-2001")
        reservation.checkOutDate = stringToInstant("03-01-2001")
        takenRoom.reservationList.add(reservation)
        roomRepository.save(takenRoom)

        def request = HttpRequest.newBuilder(URI.create(baseUrl + "/management/availability/01-01-2001/05-01-2001"))
                .headers("Content-Type", APPLICATION_JSON_VALUE)
                .GET()
                .build()
        when:
        def resp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        then:
        resp.get().statusCode() == 200
        resp.get().body().contains(freeRoom.number.toString())
        resp.get().body().contains(freeRoom.price.toString())

    }

    private Instant stringToInstant(String date) {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
    }

    private String jsonInString(ReservationRequest reservationRequest) {
        objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(reservationRequest)
    }

    void await(double seconds, Closure<?> conditions) {
        new PollingConditions().within(seconds, conditions)
    }
}
