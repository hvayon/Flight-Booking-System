package ru.hvayon.BonusService.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/* JPA */
@Table(name = "privilege_history")
@Entity // Все поля класса будут автоматически связаны со столбцами таблицы
/* Lombok */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class PrivilegeHistory {
    private static final String PRIVILEGE_ID_COLUMN = "privilege_id";
    private static final String TICKET_UID_COLUMN = "ticket_uid";
    private static final String BALANCE_DIFF_COLUMN = "balance_diff";
    private static final String OPERATION_TYPE_COLUMN = "operation_type";
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
    @Id
    private int id;
    @ManyToOne
    @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    private Privilege privilege;
    @JoinColumn(name = TICKET_UID_COLUMN, nullable = false)
    private UUID ticketUid;
    @Column(name = "datetime")
    private String date;
    @Column(name = BALANCE_DIFF_COLUMN, nullable = false)
    private int balanceDiff;

    @Column(name = OPERATION_TYPE_COLUMN, nullable = false)
    private String operationType;

}
