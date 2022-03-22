package lv.home.reservation.reservation.http

import lv.home.reservation.reservation.http.model.RoomDto
import lv.home.reservation.reservation.jpa.model.Room
import lv.home.reservation.reservation.jpa.repository.RoomRepository
import org.springframework.beans.factory.annotation.Autowired

import java.net.http.HttpRequest
import java.net.http.HttpResponse

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

class RoomControllerTest extends WebIntegrationSpec implements RoomFixture {

    @Autowired
    private RoomRepository repository

    def 'delete room data'() {
        given:
        def room = getRoom()
        room = repository.save(room)
        def request = HttpRequest.newBuilder(URI.create(baseUrl + "/room/$room.roomId"))
                .headers("Content-Type", APPLICATION_JSON_VALUE)
                .DELETE()
                .build()

        when:
        def resp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        then:
        resp.get().statusCode() == 200
        repository.findAll().size() == 0

    }

    def 'update room data'() {
        given:
        def room = getRoom()
        room = repository.save(room)

        def number = 133
        def request = HttpRequest.newBuilder(URI.create(baseUrl + "/room/$room.roomId"))
                .headers("Content-Type", APPLICATION_JSON_VALUE)
                .PUT(HttpRequest.BodyPublishers.ofString(getRoomDtoString(number)))
                .build()

        when:
        def resp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        then:
        resp.get().statusCode() == 200
        repository.findById(room.roomId).get().number == number

    }

    def 'saves room and get from location header'() {
        given:
        def number = 133
        String requestBody = getRoomDtoString(133)

        def request = HttpRequest.newBuilder(URI.create(baseUrl + "/room/"))
                .headers("Content-Type", APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build()

        when:
        def resp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        then:
        resp.get().statusCode() == 201
        resp.get().headers().allValues('location')[0] != null

        when:
        request = HttpRequest.newBuilder(URI.create(baseUrl + "/room" + resp.get().headers().allValues('location')[0]))
                .headers("Content-Type", APPLICATION_JSON_VALUE)
                .GET()
                .build()
        resp = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())

        then:
        resp.get().statusCode() == 200
        resp.get().body().contains(number.toString())
    }

    String getRoomDtoString(int number = 133, double price = 10.0) {
        def roomDto = RoomDto.builder()
                .number(number)
                .price(price)
                .build()

        String requestBody = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(roomDto)
        return requestBody
    }
}
