package ru.hvayon.TicketService.service;

import org.springframework.stereotype.Service;
import ru.hvayon.TicketService.domain.Ticket;
import ru.hvayon.TicketService.request.TicketRequest;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public interface TicketService {
    public Optional<Ticket> getTickets(String username);
    public Ticket getTicketByUid(UUID uuid, String username);
    public UUID addTicket(TicketRequest request);
    Ticket updateTicket(String username, UUID ticketUid, Map<String, Object> fields);
}