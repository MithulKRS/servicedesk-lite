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
        this.dao = dao;
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    public Ticket createTicket(String title, String description,
                               String requester, String priorityStr) throws SQLException {
        validate(title, "Title");
        validate(requester, "Requester");

        Priority priority;
        try {
            priority = Priority.valueOf(priorityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid priority: " + priorityStr + ". Use LOW, MEDIUM, or HIGH.");
        }

        Ticket t = new Ticket(title.trim(), description, requester.trim(), priority);
        long id = dao.createTicket(t);
        t.setId(id);
        return t;
    }

    // ── LIST ALL ──────────────────────────────────────────────────────────────
    public List<Ticket> listAll() throws SQLException {
        return dao.findAll();
    }

    // ── FIND BY ID ────────────────────────────────────────────────────────────
    public Ticket findById(long id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("ID must be a positive number.");
        Ticket t = dao.findById(id);
        if (t == null) throw new IllegalArgumentException("No ticket found with ID: " + id);
        return t;
    }

    // ── UPDATE STATUS ─────────────────────────────────────────────────────────
    public void updateStatus(long id, String newStatusStr) throws SQLException {
        Status newStatus;
        try {
            newStatus = Status.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid status: " + newStatusStr + ". Use OPEN, IN_PROGRESS, or RESOLVED.");
        }

        Ticket existing = findById(id);          // validates existence

        // Enforce simple forward-only transitions
        Status current = existing.getStatus();
        if (current == Status.RESOLVED && newStatus != Status.RESOLVED) {
            throw new IllegalStateException(
                "Ticket #" + id + " is already RESOLVED and cannot be re-opened.");
        }

        dao.updateStatus(id, newStatus);
    }

    // ── FILTER ────────────────────────────────────────────────────────────────
    public List<Ticket> filterByStatus(String statusStr) throws SQLException {
        try {
            return dao.findByStatus(Status.valueOf(statusStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid status: " + statusStr + ". Use OPEN, IN_PROGRESS, or RESOLVED.");
        }
    }

    public List<Ticket> filterByPriority(String priorityStr) throws SQLException {
        try {
            return dao.findByPriority(Priority.valueOf(priorityStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid priority: " + priorityStr + ". Use LOW, MEDIUM, or HIGH.");
        }
    }

    // ── REPORT ────────────────────────────────────────────────────────────────
    public Map<String, Integer> getStatusSummary() throws SQLException {
        return dao.getStatusSummary();
    }

    // ── PRIVATE ───────────────────────────────────────────────────────────────
    private void validate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }
}
