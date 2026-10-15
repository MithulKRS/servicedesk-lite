
package com.servicedesk;

import com.servicedesk.dao.TicketDAO;
import com.servicedesk.model.Ticket;
import com.servicedesk.model.Ticket.Priority;
import com.servicedesk.model.Ticket.Status;
import com.servicedesk.service.TicketService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("TicketService unit tests")
class TicketServiceTest {

    @Mock
    private TicketDAO dao;

    private TicketService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TicketService(dao);
    }

    // ── CREATE TICKET ───────────────────────────────────────────────

    @Test
    @DisplayName("Creates ticket with valid input")
    void createTicket_validInput() throws SQLException {
        when(dao.createTicket(any())).thenReturn(1L);

        Ticket ticket = service.createTicket(
                "Fix printer", "It jams", "Alice", "HIGH");

        assertEquals(1L, ticket.getId());
        assertEquals("Fix printer", ticket.getTitle());
        assertEquals(Priority.HIGH, ticket.getPriority());
        assertEquals(Status.OPEN, ticket.getStatus());

        verify(dao).createTicket(any(Ticket.class));
    }

    @Test
    @DisplayName("Trims title and requester")
    void createTicket_trimsInput() throws SQLException {
        when(dao.createTicket(any())).thenReturn(2L);

        Ticket ticket = service.createTicket(
                "  Fix printer  ", "Description", "  Alice  ", " high ");

        assertEquals("Fix printer", ticket.getTitle());
        assertEquals("Alice", ticket.getRequester());
        assertEquals(Priority.HIGH, ticket.getPriority());
    }

    @Test
    @DisplayName("Rejects blank title")
    void createTicket_blankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("", "desc", "Bob", "LOW"));

        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("   ", "desc", "Bob", "LOW"));

        verifyNoInteractions(dao);
    }

    @Test
    @DisplayName("Rejects null title")
    void createTicket_nullTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket(null, "desc", "Bob", "LOW"));

        verifyNoInteractions(dao);
    }

    @Test
    @DisplayName("Rejects blank requester")
    void createTicket_blankRequester() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("Title", "desc", "  ", "LOW"));
    }

    @Test
    @DisplayName("Rejects null requester")
    void createTicket_nullRequester() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("Title", "desc", null, "LOW"));
    }

    @Test
    @DisplayName("Rejects invalid priority")
    void createTicket_invalidPriority() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("Title", "desc", "Bob", "CRITICAL"));
    }

    @Test
    @DisplayName("Rejects null priority")
    void createTicket_nullPriority() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("Title", "desc", "Bob", null));
    }

    @Test
    @DisplayName("Rejects blank priority")
    void createTicket_blankPriority() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("Title", "desc", "Bob", "   "));
    }

    // ── FIND BY ID ──────────────────────────────────────────────────

    @Test
    @DisplayName("Returns ticket when found")
    void findById_found() throws SQLException {
        Ticket ticket = makeTicket(5L, Status.OPEN);
        when(dao.findById(5L)).thenReturn(ticket);

        assertEquals(ticket, service.findById(5L));
    }

    @Test
    @DisplayName("Rejects nonexistent ticket")
    void findById_notFound() throws SQLException {
        when(dao.findById(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.findById(99L));
    }

    @Test
    @DisplayName("Rejects non-positive ticket IDs")
    void findById_invalidId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.findById(0L));

        assertThrows(IllegalArgumentException.class,
                () -> service.findById(-1L));

        verifyNoInteractions(dao);
    }

    // ── UPDATE STATUS ──────────────────────────────────────────────

    @Test
    @DisplayName("Updates status successfully")
    void updateStatus_valid() throws SQLException {
        when(dao.findById(1L)).thenReturn(makeTicket(1L, Status.OPEN));
        when(dao.updateStatus(1L, Status.IN_PROGRESS)).thenReturn(true);

        assertDoesNotThrow(
                () -> service.updateStatus(1L, "IN_PROGRESS"));

        verify(dao).updateStatus(1L, Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Accepts status regardless of letter case")
    void updateStatus_caseInsensitive() throws SQLException {
        when(dao.findById(1L)).thenReturn(makeTicket(1L, Status.OPEN));
        when(dao.updateStatus(1L, Status.IN_PROGRESS)).thenReturn(true);

        assertDoesNotThrow(
                () -> service.updateStatus(1L, "in_progress"));

        verify(dao).updateStatus(1L, Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Rejects invalid status")
    void updateStatus_invalidStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(1L, "CLOSED"));

        verifyNoInteractions(dao);
    }

    @Test
    @DisplayName("Rejects null status")
    void updateStatus_nullStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(1L, null));

        verifyNoInteractions(dao);
    }

    @Test
    @DisplayName("Rejects blank status")
    void updateStatus_blankStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(1L, "   "));

        verifyNoInteractions(dao);
    }

    @Test
    @DisplayName("Rejects non-positive ID when updating")
    void updateStatus_invalidId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(0L, "OPEN"));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(-1L, "OPEN"));
    }

    @Test
    @DisplayName("Rejects updates to nonexistent tickets")
    void updateStatus_ticketNotFound() throws SQLException {
        when(dao.findById(99L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(99L, "IN_PROGRESS"));

        verify(dao, never()).updateStatus(anyLong(), any(Status.class));
    }

    @Test
    @DisplayName("Prevents reopening a resolved ticket")
    void updateStatus_resolvedToOpen_throws() throws SQLException {
        when(dao.findById(2L)).thenReturn(makeTicket(2L, Status.RESOLVED));

        assertThrows(IllegalStateException.class,
                () -> service.updateStatus(2L, "OPEN"));

        verify(dao, never()).updateStatus(anyLong(), any(Status.class));
    }

    @Test
    @DisplayName("Prevents moving a resolved ticket back to progress")
    void updateStatus_resolvedToInProgress_throws() throws SQLException {
        when(dao.findById(3L)).thenReturn(makeTicket(3L, Status.RESOLVED));

        assertThrows(IllegalStateException.class,
                () -> service.updateStatus(3L, "IN_PROGRESS"));

        verify(dao, never()).updateStatus(anyLong(), any(Status.class));
    }

    // ── FILTER BY STATUS ───────────────────────────────────────────

    @Test
    @DisplayName("Filters tickets by status")
    void filterByStatus_valid() throws SQLException {
        Ticket ticket = makeTicket(1L, Status.OPEN);
        when(dao.findByStatus(Status.OPEN)).thenReturn(List.of(ticket));

        List<Ticket> result = service.filterByStatus("OPEN");

        assertEquals(1, result.size());
        assertEquals(ticket, result.get(0));
    }

    @Test
    @DisplayName("Rejects invalid status filter")
    void filterByStatus_invalid() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filterByStatus("DONE"));
    }

    @Test
    @DisplayName("Rejects null status filter")
    void filterByStatus_null() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filterByStatus(null));
    }

    @Test
    @DisplayName("Rejects blank status filter")
    void filterByStatus_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filterByStatus("   "));
    }

    // ── FILTER BY PRIORITY ─────────────────────────────────────────

    @Test
    @DisplayName("Filters tickets by priority")
    void filterByPriority_valid() throws SQLException {
        Ticket ticket = makeTicket(1L, Status.OPEN);
        when(dao.findByPriority(Priority.HIGH)).thenReturn(List.of(ticket));

        List<Ticket> result = service.filterByPriority("HIGH");

        assertEquals(1, result.size());
        assertEquals(ticket, result.get(0));
    }

    @Test
    @DisplayName("Rejects invalid priority filter")
    void filterByPriority_invalid() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filterByPriority("URGENT"));
    }

    @Test
    @DisplayName("Rejects null priority filter")
    void filterByPriority_null() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filterByPriority(null));
    }

    @Test
    @DisplayName("Rejects blank priority filter")
    void filterByPriority_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filterByPriority("   "));
    }

    // ── STATUS SUMMARY ─────────────────────────────────────────────

    @Test
    @DisplayName("Returns status summary")
    void getStatusSummary() throws SQLException {
        when(dao.getStatusSummary())
                .thenReturn(Map.of("OPEN", 3, "RESOLVED", 1));

        Map<String, Integer> summary = service.getStatusSummary();

        assertEquals(3, summary.get("OPEN"));
        assertEquals(1, summary.get("RESOLVED"));
    }

    // ── HELPER ──────────────────────────────────────────────────────

    private Ticket makeTicket(long id, Status status) {
        Ticket ticket = new Ticket(
                "Sample", "desc", "Alice", Priority.MEDIUM);

        ticket.setId(id);
        ticket.setStatus(status);

        return ticket;
    }
}

