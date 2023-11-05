package ru.hvayon.FlightService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hvayon.FlightService.domain.Flight;
import ru.hvayon.FlightService.model.FlightResponse;
import ru.hvayon.FlightService.model.FlightResponseList;
import ru.hvayon.FlightService.service.FlightService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FlightServiceController {

    private final FlightService flightService;

    public FlightServiceController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/manage/health")
    void getManegeHealth() {
        System.out.println("Flight service alive");
    }

    @GetMapping("/api/v1/flights")
    public FlightResponseList getFlights(@RequestParam(name="page",required = false) Integer page,
                                                      @RequestParam(name = "size", required = false) Integer size){
        List<Flight> flights = flightService.getFlights(page, size).getContent();
        List<FlightResponse> flightResponses = new ArrayList<>();
        for (Flight flight: flights) {
            flightResponses.add(FlightResponse.build(
                    flight.getFlightNumber(),
                    String.format("%s %s", flight.getFromAirportId().getCity(), flight.getFromAirportId().getName()),
                    String.format("%s %s", flight.getToAirportId().getCity(), flight.getToAirportId().getName()),
                    flight.getDatetime(),
                    flight.getPrice()));
        }
        return FlightResponseList.build(page, size, flights.size(), flightResponses);
    }
}
