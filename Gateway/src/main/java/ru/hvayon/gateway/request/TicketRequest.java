package ru.hvayon.gateway.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class TicketRequest {
    private String flightNumber;
    private int price;
    private boolean paidFromBalance;
}
