package ru.hvayon.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Setter
@Getter
public class PrivilegeWithHistoryResponse {
    int balance;
    String status;
    List<BalanceHistory> history;
}