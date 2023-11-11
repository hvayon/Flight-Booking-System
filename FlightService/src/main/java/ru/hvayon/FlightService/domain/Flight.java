package ru.hvayon.FlightService.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* JPA */
@Table(name = "flight")
@Entity // Все поля класса будут автоматически связаны со столбцами таблицы
/* Lombok */
@Getter
@Setter
@NoArgsConstructor
public class Flight {
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
    @Id
    private int id;
    private String flightNumber;
    private String datetime;
    @ManyToOne
    @JoinColumn(name = "from_airport_id", referencedColumnName = "id")
    private Airport fromAirportId;
    @ManyToOne
    @JoinColumn(name = "to_airport_id", referencedColumnName = "id")
    private Airport toAirportId;
    private int price;
}
