package ru.hvayon.BonusService.service;

import org.springframework.stereotype.Service;
import ru.hvayon.BonusService.domain.Privilege;
import ru.hvayon.BonusService.domain.PrivilegeHistory;
import ru.hvayon.BonusService.request.PrivilegeHistoryRecordRequest;
import ru.hvayon.BonusService.response.OperationType;

import java.util.List;
import java.util.UUID;

@Service
public interface PrivilegeService {
    public Privilege getPrivilege(String username);
    public List<PrivilegeHistory> getPrivilegeHistory(String username);
    public int addHistoryRecord(String username, PrivilegeHistoryRecordRequest privilegeHistoryRecordRequest);
//    Privilege updatePrivilege(String username, Map<String,Object> fields);
    PrivilegeHistory getPrivilegeHistoryOfTicket(String username, UUID ticketUid);

    void giveBonuses(Privilege privilege, int price);

    OperationType setOperationType(boolean paidFromBalance, int balance);

    void payFromBalance(Privilege privilege);
}