
package com.servicedesk.service;

import com.servicedesk.dao.TicketDAO;
import com.servicedesk.model.Ticket;
import com.servicedesk.model.Ticket.Priority;
import com.servicedesk.model.Ticket.Status;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TicketService {

    private final TicketDAO dao;

    public TicketService(TicketDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException(
                "TicketDAO cannot be null."
            );
        }
        this.dao = dao;
    }

    // CREATE
    public Ticket createTicket(
            String title,
            String description,
            String requester,
            String priorityStr
    ) throws SQLException {

        validate(title, "Title");
        validate(requester, "Requester");

        Priority priority = parsePriority(priorityStr);

        Ticket ticket = new Ticket(
            title.trim(),
            description,
            requester.trim(),
            priority
        );

        long id = dao.createTicket(ticket);
        ticket.setId(id);

        return ticket;
    }

    // LIST ALL
    public List<Ticket> listAll() throws SQLException {
        return dao.findAll();
    }

    // FIND BY ID
    public Ticket findById(long id) throws SQLException {
        validateId(id);

        Ticket ticket = dao.findById(id);

        if (ticket == null) {
            throw new IllegalArgumentException(
                "No ticket found with ID: " + id
            );
        }

        return ticket;
    }

    // UPDATE STATUS
    public void updateStatus(
            long id,
            String newStatusStr
    ) throws SQLException {

        validateId(id);
        Status newStatus = parseStatus(newStatusStr);

        // Validate that the ticket exists.
        Ticket existing = findById(id);
        Status current = existing.getStatus();

        // Resolved tickets cannot be reopened.
        if (current == Status.RESOLVED
                && newStatus != Status.RESOLVED) {
            throw new IllegalStateException(
                "Ticket #" + id
                    + " is already RESOLVED and cannot be re-opened."
            );
        }

        dao.updateStatus(id, newStatus);
    }

    // FILTER BY STATUS
    public List<Ticket> filterByStatus(
            String statusStr
    ) throws SQLException {
        Status status = parseStatus(statusStr);
        return dao.findByStatus(status);
    }

    // FILTER BY PRIORITY
    public List<Ticket> filterByPriority(
            String priorityStr
    ) throws SQLException {
        Priority priority = parsePriority(priorityStr);
        return dao.findByPriority(priority);
    }

    // STATUS SUMMARY
    public Map<String, Integer> getStatusSummary()
            throws SQLException {
        return dao.getStatusSummary();
    }

    // VALIDATION HELPERS
    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                fieldName + " cannot be empty."
            );
        }
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                "ID must be a positive number."
            );
        }
    }

    private Priority parsePriority(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                "Priority cannot be empty. "
                    + "Use LOW, MEDIUM, or HIGH."
            );
        }

        try {
            return Priority.valueOf(
                value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid priority: " + value.trim()
                    + ". Use LOW, MEDIUM, or HIGH."
            );
        }
    }

    private Status parseStatus(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                "Status cannot be empty. "
                    + "Use OPEN, IN_PROGRESS, or RESOLVED."
            );
        }

        try {
            return Status.valueOf(
                value.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid status: " + value.trim()
                    + ". Use OPEN, IN_PROGRESS, or RESOLVED."
            );
        }
    }
}
