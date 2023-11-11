package ru.hvayon.TicketService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hvayon.TicketService.domain.Ticket;
import ru.hvayon.TicketService.request.TicketRequest;
import ru.hvayon.TicketService.service.TicketService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
public class TicketServiceController {

    private final TicketService ticketService;

    public TicketServiceController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/manage/health")
    public ResponseEntity<Void> status() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/v1/tickets")
    public Optional<Ticket> getTicketsOfUser(@RequestHeader(name = "X-User-Name") String username) {
        return ticketService.getTickets(username);
    }

    @GetMapping("/api/v1/tickets/{ticketUid}")
    public Ticket getTicketByUid(@PathVariable UUID ticketUid, @RequestHeader(name = "X-User-Name") String username) {
        return ticketService.getTicketByUid(ticketUid, username);
    }

    @PostMapping("/api/v1/tickets")
    public UUID addTicket(@RequestBody TicketRequest ticket) {
        return ticketService.addTicket(ticket);
    }

    @PatchMapping("/api/v1/tickets/{ticketUid}")
    public Ticket updateOperationType(@PathVariable UUID ticketUid, @RequestHeader(name = "X-User-Name") String username, @RequestBody Map<String, Object> fields) {
        return ticketService.updateTicket(username, ticketUid, fields);
    }
}
