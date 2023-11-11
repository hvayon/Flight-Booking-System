package ru.hvayon.FlightService.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.hvayon.FlightService.domain.Flight;

@Service
public interface FlightService {
    public Page<Flight> getFlights(int page, int size);

    Flight getFlightByFlightNumber(String flightNumber);
}