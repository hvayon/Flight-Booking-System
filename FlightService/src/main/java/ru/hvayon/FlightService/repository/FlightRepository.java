package ru.hvayon.FlightService.repository;

import ru.hvayon.FlightService.domain.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {
    Optional<Flight> findByFlightNumber(String flightNumber);
    Page<Flight> findByFlightNumber(String flightNumber, Pageable pageable);
}
