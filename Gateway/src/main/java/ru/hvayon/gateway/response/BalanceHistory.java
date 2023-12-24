package ru.hvayon.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class BalanceHistory {
    String date;
    UUID ticketUid;
    int balanceDiff;
    String operationType;
}
