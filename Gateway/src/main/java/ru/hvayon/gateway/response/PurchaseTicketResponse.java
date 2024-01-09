package ru.hvayon.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class PurchaseTicketResponse {
    private UUID ticketUid;
    private String flightNumber;
    private String fromAirport;
    private String toAirport;
    private String date;
    private int price;
    int paidByMoney;
    int paidByBonuses;
    private String status;
    private PrivilegeShortInfo privilegesInfo;

}


