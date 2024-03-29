package ru.hvayon.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.hvayon.gateway.request.AddTicketRequest;
import ru.hvayon.gateway.request.PrivilegeHistoryRequest;
import ru.hvayon.gateway.request.TicketRequest;
import ru.hvayon.gateway.response.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@PropertySource("classpath:application.properties")
public class GatewayController {
    @Value("${flight_service.host}")
    private String FLIGHT_SERVICE;

    @Value("${ticket_service.host}")
    private String TICKET_SERVICE;

    @Value("${bonus_service.host}")
    private String BONUS_SERVICE;

    private final String GET_FLIGHTS_URL = "/api/v1/flights?page={page}&size={size}";
    private final String GET_FLIGHT_BY_NUMBER_URL = "/api/v1/flights/{flightNumber}";
    private final String GET_TICKETS_URL = "/api/v1/tickets";

    private final String GET_TICKET_BY_UID_URL = "/api/v1/tickets/{ticketUid}";
    private final String GET_PRIVILEGE_URL = "/api/v1/privilege";
    private final String GET_PRIVILEGE_HISTORY_URL = "/api/v1/privilege/history";
    private static String GET_PRIVILEGE_HISTORY_BY_TICKET_UID_URL = "/api/v1/privilege/history/{ticketUid}";

    @GetMapping("/flights")
    public FlightResponseList getFlights(@RequestParam int page, @RequestParam int size) {
        return new RestTemplate().getForObject(FLIGHT_SERVICE + GET_FLIGHTS_URL, FlightResponseList.class, page, size);
    }

    @PostMapping("/tickets")
    public PurchaseTicketResponse buyTicket(@RequestHeader(name = "X-User-Name") String username, @RequestBody TicketRequest ticket) throws Exception {

        // проверяем, что рейс с таким номером существует
        FlightResponse flightResponse = new RestTemplate().getForObject(
                FLIGHT_SERVICE + GET_FLIGHT_BY_NUMBER_URL, FlightResponse.class, ticket.getFlightNumber());

        // формируем запрос на покупку билета
        AddTicketRequest addTicketRequest = AddTicketRequest.build(
                username,
                ticket.getFlightNumber(),
                ticket.getPrice(),
                "PAID"
        );

        // покупка билета
        ResponseEntity<UUID> responseEntity = new RestTemplate().postForEntity(
                TICKET_SERVICE + GET_TICKETS_URL, addTicketRequest, UUID.class);

        // получение uuid билета
        UUID ticketUid = responseEntity.getBody();


        // получаем информацию о бонусах
        try {
            PrivilegeShortInfo privilegeResponse = getPrivilegeInfo(username);
            int bonusBalance = privilegeResponse.getBalance();

            int paidByMoney = ticket.getPrice();
            int paidByBonuses = 0;

            // иcпользуются ли бонусы в оплате
            if (ticket.isPaidFromBalance()) {
                // потратить все бонусы
                addHistoryRecord(ticketUid, bonusBalance, "DEBIT_THE_ACCOUNT", username);
                updateBalance(0, username);
                paidByBonuses = bonusBalance;
                paidByMoney = paidByMoney - bonusBalance;
            } else {
                // добавить бонусы - 10% от оплаты билета
                addHistoryRecord(ticketUid, ticket.getPrice() / 10, "FILL_IN_BALANCE", username);
                updateBalance(bonusBalance + (ticket.getPrice() / 10), username);
            }
            // get updated balance info
            PrivilegeShortInfo updatedPrivilege = getPrivilegeInfo(username);

            // create response
            return PurchaseTicketResponse.build(
                    ticketUid,
                    flightResponse.getFlightNumber(),
                    flightResponse.getFromAirport(),
                    flightResponse.getToAirport(),
                    flightResponse.getDate(),
                    flightResponse.getPrice(),
                    paidByMoney,
                    paidByBonuses,
                    "PAID",
                    updatedPrivilege
            );
        } catch (HttpClientErrorException e) {
            throw new Exception("Privilege of user " + username + " not found");
        }
    }

    // Получаем информацию о билете
    @GetMapping("/tickets")
    public TicketResponse[] getTickets(@RequestHeader(name = "X-User-Name") String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", name);
        RestTemplate restTemplate = new RestTemplate();
        TicketResponse[] tickets = restTemplate.exchange(
                TICKET_SERVICE + GET_TICKETS_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TicketResponse[].class).getBody();
        for (TicketResponse ticket : tickets) {
            FlightResponse flight = new RestTemplate().getForObject(
                    FLIGHT_SERVICE + GET_FLIGHT_BY_NUMBER_URL,
                    FlightResponse.class,
                    ticket.getFlightNumber());
            ticket.setDate(flight.getDate());
            ticket.setFromAirport(flight.getFromAirport());
            ticket.setToAirport(flight.getToAirport());
        }
        return tickets;
    }

    @GetMapping("/tickets/{ticketUid}")
    public TicketResponse getTicket(@PathVariable("ticketUid") UUID ticketUid, @RequestHeader(name = "X-User-Name") String username) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TicketResponse> ticketResponse = restTemplate.exchange(
                TICKET_SERVICE + GET_TICKET_BY_UID_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                TicketResponse.class,
                ticketUid,
                username);
        TicketResponse ticket = ticketResponse.getBody();
        return ticket;
    }

    @GetMapping("/privilege")
    public PrivilegeWithHistoryResponse getPrivilege(@RequestHeader("X-User-Name") String username) throws Exception {
        try {
            PrivilegeShortInfo privilege = getPrivilegeInfo(username);
            List<BalanceHistory> balanceList = new ArrayList<>(Arrays.asList(getBalanceHistory(username)));
            return PrivilegeWithHistoryResponse.build(privilege.getBalance(), privilege.getStatus(), balanceList);
        } catch (HttpClientErrorException e) {
            throw new Exception("Privilege of user " + username + " not found");
        }
    }

    private BalanceHistory[] getBalanceHistory(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", username);
        return new RestTemplate().exchange(
                BONUS_SERVICE + GET_PRIVILEGE_HISTORY_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BalanceHistory[].class
        ).getBody();
    }
    private PrivilegeShortInfo getPrivilegeInfo(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", username);
        ResponseEntity<PrivilegeShortInfo> privilege = new RestTemplate().exchange(
                BONUS_SERVICE + GET_PRIVILEGE_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                PrivilegeShortInfo.class
        );
        if (privilege.getStatusCode() != HttpStatusCode.valueOf(200)) {
            throw new HttpClientErrorException(HttpStatusCode.valueOf(404));
        } else {
            return privilege.getBody();
        }
    }

    private String addHistoryRecord(UUID ticketUid, int bonusAmount, String operationType, String username) {
        PrivilegeHistoryRequest request = PrivilegeHistoryRequest.build(
                ticketUid,
                bonusAmount,
                operationType
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Name", username);
        HttpEntity<PrivilegeHistoryRequest> historyRecord = new HttpEntity<>(request, headers);
        ResponseEntity<Void> historyResponseEntity = new RestTemplate().postForEntity(
                BONUS_SERVICE + GET_PRIVILEGE_HISTORY_URL,
                historyRecord,
                Void.class
        );
        return historyResponseEntity.getHeaders().get("Location").toString();
    }

    // обновляем бонусный баланс в таблице
    private void updateBalance(int balance, String username) {
        HttpHeaders headers =  new HttpHeaders();
        headers.set("X-User-Name", username);
        Map<String, Object> fields = new HashMap<>();
        fields.put("balance", balance);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.exchange(
                BONUS_SERVICE + GET_PRIVILEGE_URL,
                HttpMethod.PATCH,
                new HttpEntity<>(fields, headers),
                PrivilegeShortInfo.class
        );
    }
}
