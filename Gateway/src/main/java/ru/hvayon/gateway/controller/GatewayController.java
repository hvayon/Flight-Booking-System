package ru.hvayon.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.hvayon.gateway.response.FlightResponseList;

@RestController
@RequestMapping("/api/v1")
@PropertySource("classpath:application.properties")
public class GatewayController {
    @Value("${flight_service.host}")
    private String FLIGHT_SERVICE;

    private static String GET_FLIGHTS_URL = "/api/v1/flights?page={page}&size={size}";

    @GetMapping("/flights")
    public FlightResponseList getFlights(@RequestParam int page, @RequestParam int size) {
        return new RestTemplate().getForObject(FLIGHT_SERVICE + GET_FLIGHTS_URL, FlightResponseList.class, page - 1, size);
    }
}
