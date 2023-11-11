package ru.hvayon.TicketService.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import ru.hvayon.TicketService.domain.Ticket;
import ru.hvayon.TicketService.repository.TicketRepository;
import ru.hvayon.TicketService.request.TicketRequest;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> getTickets(String username) {
        return ticketRepository.findAllByUsername(username);
    }

    @Override
    public Ticket getTicketByUid(UUID uuid, String username) {
        Optional<Ticket> ticket = ticketRepository.findByTicketUidAndUsername(uuid, username);
        if (ticket.isPresent()) {
            return ticket.get();
        } else {
            throw new EntityNotFoundException("Ticket with uid=" + uuid + " not found for user " + username);
        }
    }

    @Override
    public UUID addTicket(TicketRequest request) {
        Ticket newTicket = Ticket.build(
                0,
                UUID.randomUUID(),
                request.getUsername(),
                request.getFlight_number(),
                request.getPrice(),
                request.getStatus()
        );
        ticketRepository.save(newTicket);
        return newTicket.getTicketUid();
    }

    @Override
    public Ticket updateTicket(String username, UUID ticketUid, Map<String, Object> fields) {
        Optional<Ticket> ticket = ticketRepository.findByTicketUidAndUsername(ticketUid, username);
        if (ticket.isPresent()) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(Ticket.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, ticket.get(), value);
            });
            return ticketRepository.save(ticket.get());
        } else {
            return null;
        }
    }
}
