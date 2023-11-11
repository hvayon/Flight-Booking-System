package ru.hvayon.TicketService.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class TicketResponse {
    String flightNumber;
    String fromAirport;
    String toAirport;
    String date;
    int price;
}
