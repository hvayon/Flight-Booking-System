package ru.hvayon.gateway.response;

import java.util.UUID;

public class PurchaseTicketResponse {
    private UUID ticketUid;
    private String flightNumber;
    private String fromAirport;
    private String toAirport;
    private String date;
    private int price;
    private boolean paidByMoney;
    private boolean paidByBonuses;
    private String status;
    private PrivilegesInfo privilegesInfo;

}


