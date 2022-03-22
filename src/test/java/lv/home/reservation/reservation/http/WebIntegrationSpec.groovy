package lv.home.reservation.reservation.http

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import java.net.http.HttpClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class WebIntegrationSpec extends Specification{

    def httpClient = HttpClient.newHttpClient()

    @Autowired
    ObjectMapper objectMapper

    @LocalServerPort
    String port

    String getBaseUrl(){
        'http://localhost:' + port
    }
}
