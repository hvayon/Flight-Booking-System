package ru.hvayon.TicketService.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
public class TicketRequest {
    String username;
    String flight_number;
    int price;
    String status;
}
