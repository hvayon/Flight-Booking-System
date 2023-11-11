package ru.hvayon.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.hvayon.gateway.request.TicketRequest;
import ru.hvayon.gateway.response.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@PropertySource("classpath:application.properties")
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${flight_service.host}")
    private String FLIGHT_SERVICE;

    @Value("${ticket_service.host}")
    private String TICKET_SERVICE;

    @Value("${bonus_service.host}")
    private String BONUS_SERVICE;

    private final String GET_FLIGHTS_URL = "/api/v1/flights?page={page}&size={size}";
    private final String GET_FLIGHT_BY_NUMBER_URL = "/api/v1/flights/{flightNumber}";

    private final String GET_TICKETS_URL = "/api/v1/tickets";

    @GetMapping("/flights")
    public FlightResponseList getFlights(@RequestParam int page, @RequestParam int size) {
        return new RestTemplate().getForObject(FLIGHT_SERVICE + GET_FLIGHTS_URL, FlightResponseList.class, page - 1, size);
    }

    @PostMapping("/tickets")
    public PurchaseTicketResponse buyTicket(@RequestHeader(name = "X-User-Name") String username, @RequestBody TicketRequest ticket) throws Exception {
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        // проверяем, что рейс с таким номером существует
        FlightResponse flightResponse = template.getForObject(
                FLIGHT_SERVICE + GET_FLIGHT_BY_NUMBER_URL, FlightResponse.class, ticket.getFlightNumber());

        // формируем запрос на покупку билета
        TicketRequest ticketRequest = TicketRequest.build(
                ticket.getName(),
                ticket.getFlightNumber(),
                ticket.getPrice(),
                ticket.isPaidFromBalance()
        );

        // покупка билета
        ResponseEntity<UUID> responseEntity = template.postForEntity(
                TICKET_SERVICE + GET_TICKETS_URL, ticketRequest, UUID.class);

        // получение uuid билета
        UUID ticketUid = responseEntity.getBody();

    }
}
