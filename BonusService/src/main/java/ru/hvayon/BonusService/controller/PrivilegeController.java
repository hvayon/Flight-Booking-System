package ru.hvayon.BonusService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.hvayon.BonusService.domain.Privilege;
import ru.hvayon.BonusService.domain.PrivilegeHistory;
import ru.hvayon.BonusService.request.PrivilegeHistoryRecordRequest;
import ru.hvayon.BonusService.response.OperationType;
import ru.hvayon.BonusService.service.PrivilegeService;

import java.util.List;
import java.util.UUID;

@RestController
public class PrivilegeController {
    private PrivilegeService privilegeService;

    @GetMapping("/api/v1/privilege")
    public Privilege getPrivilege(@RequestHeader(name = "X-User-Name") String username)  {
        return privilegeService.getPrivilege(username);
    }

    @GetMapping("/api/v1/privilege/history")
    public List<PrivilegeHistory> getPrivilegeHistory(@RequestHeader(name = "X-User-Name") String username) {
        return privilegeService.getPrivilegeHistory(username);
    }

    // api получения истории бонусного баланса билета
    @GetMapping("/api/v1/privilege/history/{ticketUid}")
    public PrivilegeHistory getPrivilegeHistoryOfTicket(@PathVariable UUID ticketUid, @RequestHeader(name = "X-User-Name") String username) {
        return privilegeService.getPrivilegeHistoryOfTicket(username, ticketUid);
    }

    @GetMapping("/manage/health")
    public ResponseEntity<Void> status() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // api для добавление записи о списании со счета
    @PostMapping("/api/v1/privilege/history")
    public ResponseEntity<Void> addHistoryRecord(@RequestHeader(name = "X-User-Name") String username, @RequestBody PrivilegeHistoryRecordRequest request) {
        int id = privilegeService.addHistoryRecord(username, request);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/api/v1/{id}")
                .buildAndExpand(id)
                .toUri()).build();
    }

    // api для обновления бонусного баланса
    @PatchMapping("/api/v1/privilege")
    public void updatePrivilege(@RequestHeader(name = "X-User-Name") String username,
                                  @RequestParam String paidFromBalance, int price, UUID ticketUid) {
        Privilege privilege = privilegeService.getPrivilege(username);
        int balance = privilege.getBalance();
        OperationType operationType = privilegeService.setOperationType(Boolean.parseBoolean(paidFromBalance), balance);
        if (Boolean.parseBoolean(paidFromBalance)) {
            privilegeService.payFromBalance(privilege);
            privilegeService.giveBonuses(privilege, price - balance);
        } else {
            privilegeService.giveBonuses(privilege, price);
        }
        privilegeService.addHistoryRecord(username, PrivilegeHistoryRecordRequest.build(ticketUid, balance, operationType.toString()));
    }

}
