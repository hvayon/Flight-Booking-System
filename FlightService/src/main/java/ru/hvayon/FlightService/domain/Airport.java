package ru.hvayon.FlightService.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* JPA */
@Table(name = "airport")
@Entity // Все поля класса будут автоматически связаны со столбцами таблицы
/* Lombok */
@Getter
@Setter
@NoArgsConstructor
public class Airport {
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    private String name;
    private String city;
    private String country;
}
