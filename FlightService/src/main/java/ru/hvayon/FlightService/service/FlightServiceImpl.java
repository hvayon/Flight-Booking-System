package ru.hvayon.FlightService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hvayon.FlightService.domain.Flight;
import ru.hvayon.FlightService.repository.FlightRepository;
@Service
public class FlightServiceImpl implements FlightService {
    @Autowired(required=true)
    private FlightRepository flightRepository;

    @Override
    public Page<Flight> getFlights(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return flightRepository.findAll(pageRequest);
    }
}
