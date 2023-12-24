package ru.hvayon.gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
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
    private final String GET_PRIVILEGE_URL = "/api/v1/privilege";
    private final String GET_PRIVILEGE_HISTORY_URL = "/api/v1/privilege/history";

    @GetMapping("/flights")
    public FlightResponseList getFlights(@RequestParam int page, @RequestParam int size) {
        return new RestTemplate().getForObject(FLIGHT_SERVICE + GET_FLIGHTS_URL, FlightResponseList.class, page - 1, size);
    }

    @PostMapping("/tickets")
    public void buyTicket(@RequestHeader(name = "X-User-Name") String username, @RequestBody TicketRequest ticket) throws Exception {
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

        // получаем информацию о бонусах
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
        ResponseEntity<PrivilegeShortInfo> bonusResponseEntity = restTemplate.exchange(
                BONUS_SERVICE + GET_PRIVILEGE_URL,
                HttpMethod.POST,
                new HttpEntity<>(fields, headers),
                PrivilegeShortInfo.class
        );
    }
}
