package ru.hvayon.BonusService.service;

import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hvayon.BonusService.domain.Privilege;
import ru.hvayon.BonusService.domain.PrivilegeHistory;
import ru.hvayon.BonusService.repository.PrivilegeHistoryRepository;
import ru.hvayon.BonusService.repository.PrivilegeRepository;
import ru.hvayon.BonusService.request.PrivilegeHistoryRecordRequest;
import ru.hvayon.BonusService.response.OperationType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.hvayon.BonusService.response.OperationType.*;

@Service
public class PrivilegeServiceImpl implements PrivilegeService {
    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PrivilegeHistoryRepository privilegeHistoryRepository;

    @Override
    public Privilege getPrivilege(String username) {
        Optional<Privilege> privilege = privilegeRepository.findByUsername(username);
        if (privilege.isPresent()) {
           return privilege.get();
        } else {
            throw new EntityNotFoundException("User" + username + "have not found privilege");
        }
    }
    @Override
    public List<PrivilegeHistory> getPrivilegeHistory(String username) {
        return privilegeHistoryRepository.findAllByPrivilege(getPrivilege(username));
    }

    @Override
    public int addHistoryRecord(String username, @NotNull PrivilegeHistoryRecordRequest privilegeHistoryRecordRequest) {
        PrivilegeHistory privilegeHistory = PrivilegeHistory.build(
                0,
                getPrivilege(username),
                privilegeHistoryRecordRequest.getTicketUid(),
                LocalDateTime.now().toString(),
                privilegeHistoryRecordRequest.getBalanceDiff(),
                privilegeHistoryRecordRequest.getOperationType()
        );
        privilegeHistoryRepository.save(privilegeHistory);
        return privilegeHistory.getId();
    }

//    @Override
//    public Privilege updatePrivilege(String username, Map<String, Object> fields) {
//        Optional<Privilege> privilegeOptional = privilegeRepository.findByUsername(username);
//        if (privilegeOptional.isPresent()) {
//            fields.forEach((key, value) -> {
//                Field field = ReflectionUtils.findField(Privilege.class, key);
//                field.setAccessible(true);
//                ReflectionUtils.setField(field, privilegeOptional.get(), value);
//            });
//            return privilegeRepository.save(privilegeOptional.get());
//        }
//        return null;
//    }

    @Override
    public PrivilegeHistory getPrivilegeHistoryOfTicket(String username, UUID ticketUid) {
        Optional<PrivilegeHistory> privilegeHistory =
                privilegeHistoryRepository.findTopByPrivilegeAndTicketUidOrderByDate(
                        getPrivilege(username),
                        ticketUid
                );
        if (privilegeHistory.isPresent()) {
            return privilegeHistory.get();
        } else {
            throw new EntityNotFoundException("Privilege history of ticket with uid=" + ticketUid + " not found");
        }
    }

    @Override
    public void giveBonuses(Privilege privilege, int price) {
        privilege.setBalance(privilege.getBalance() + price / 100 * 10);
        privilegeRepository.save(privilege);
    }

    @Override
    public OperationType setOperationType(boolean paidFromBalance, int balance) {
        if (!paidFromBalance) {
            return FILL_IN_BALANCE;
        } else {
            if (balance > 0) {
                return DEBIT_THE_ACCOUNT;
            }
            return FILLED_BY_MONEY;
        }
    }

    @Override
    public void payFromBalance(Privilege privilege) {
        privilege.setBalance(0);
        privilegeRepository.save(privilege);
    }
}
