package ru.hvayon.BonusService.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* JPA */
@Table(name = "privilege")
@Entity // Все поля класса будут автоматически связаны со столбцами таблицы
/* Lombok */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class Privilege {
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    @Column(name = "status") //значение по умолчанию
    private String status ;
    @Column(name = "balance")
    private int balance;
}
