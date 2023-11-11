package ru.hvayon.TicketService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hvayon.TicketService.domain.Ticket;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Optional<Ticket> findAllByUsername(String username);
    Optional<Ticket> findByTicketUidAndUsername(UUID uuid, String username);
}
