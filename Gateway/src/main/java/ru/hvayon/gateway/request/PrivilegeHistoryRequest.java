package ru.hvayon.gateway.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class PrivilegeHistoryRequest {
    UUID ticketUid;
    int balanceDiff;
    String operationType;
}
