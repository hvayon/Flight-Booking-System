package ru.hvayon.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
public class PrivilegeShortInfo {
    private int balance;
    private String status;
}
