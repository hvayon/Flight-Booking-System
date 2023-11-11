package ru.hvayon.TicketService.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/* JPA */
@Table(name = "ticket")
@Entity // Все поля класса будут автоматически связаны со столбцами таблицы
/* Lombok */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class Ticket {
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
    @Id
    private int id;
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ticket_uid")
    private UUID ticketUid;
    private String username;
    @Column(name = "flight_number")
    private String flightNumber;
    private int price;
    private String status;
}
