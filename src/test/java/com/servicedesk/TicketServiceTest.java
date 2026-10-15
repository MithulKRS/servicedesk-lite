package com.servicedesk;

import com.servicedesk.dao.TicketDAO;
import com.servicedesk.model.Ticket;
import com.servicedesk.model.Ticket.Priority;
import com.servicedesk.model.Ticket.Status;
import com.servicedesk.service.TicketService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TicketService unit tests")
class TicketServiceTest {

    @Mock  private TicketDAO     dao;
    private        TicketService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TicketService(dao);
    }

    // ── createTicket ──────────────────────────────────────────────────────────
    @Test
    @DisplayName("Creates ticket with valid input")
    void createTicket_validInput() throws SQLException {
        when(dao.createTicket(any())).thenReturn(1L);

        Ticket t = service.createTicket("Fix printer", "It jams", "Alice", "HIGH");

        assertEquals(1L,               t.getId());
        assertEquals("Fix printer",    t.getTitle());
        assertEquals(Priority.HIGH,    t.getPriority());
        assertEquals(Status.OPEN,      t.getStatus());
    }

    @Test
    @DisplayName("Throws on blank title")
    void createTicket_blankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("", "desc", "Bob", "LOW"));
    }

    @Test
    @DisplayName("Throws on blank requester")
    void createTicket_blankRequester() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("Title", "desc", "  ", "LOW"));
    }

    @Test
    @DisplayName("Throws on invalid priority")
    void createTicket_invalidPriority() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createTicket("Title", "desc", "Bob", "CRITICAL"));
    }

    // ── findById ──────────────────────────────────────────────────────────────
    @Test
    @DisplayName("Returns ticket when found")
    void findById_found() throws SQLException {
        Ticket t = makeTicket(5L, Status.OPEN);
        when(dao.findById(5L)).thenReturn(t);

        assertEquals(t, service.findById(5L));
    }

    @Test
    @DisplayName("Throws when ticket not found")
    void findById_notFound() throws SQLException {
        when(dao.findById(99L)).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> service.findById(99L));
    }

    @Test
    @DisplayName("Throws on non-positive ID")
    void findById_invalidId() {
        assertThrows(IllegalArgumentException.class, () -> service.findById(0L));
        assertThrows(IllegalArgumentException.class, () -> service.findById(-1L));
    }

    // ── updateStatus ──────────────────────────────────────────────────────────
    @Test
    @DisplayName("Updates status successfully")
    void updateStatus_valid() throws SQLException {
        when(dao.findById(1L)).thenReturn(makeTicket(1L, Status.OPEN));
        when(dao.updateStatus(1L, Status.IN_PROGRESS)).thenReturn(true);

        assertDoesNotThrow(() -> service.updateStatus(1L, "IN_PROGRESS"));
        verify(dao).updateStatus(1L, Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Prevents re-opening a RESOLVED ticket")
    void updateStatus_resolvedToOpen_throws() throws SQLException {
        when(dao.findById(2L)).thenReturn(makeTicket(2L, Status.RESOLVED));

        assertThrows(IllegalStateException.class,
                () -> service.updateStatus(2L, "OPEN"));
    }

    @Test
    @DisplayName("Throws on invalid status string")
    void updateStatus_invalidStatus() throws SQLException {
        when(dao.findById(1L)).thenReturn(makeTicket(1L, Status.OPEN));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateStatus(1L, "CLOSED"));
    }

    // ── filterByStatus ────────────────────────────────────────────────────────
    @Test
    @DisplayName("Filters by status")
    void filterByStatus_valid() throws SQLException {
        Ticket t = makeTicket(1L, Status.OPEN);
        when(dao.findByStatus(Status.OPEN)).thenReturn(List.of(t));

        List<Ticket> result = service.filterByStatus("OPEN");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Throws on invalid status filter")
    void filterByStatus_invalid() {
        assertThrows(IllegalArgumentException.class,
                () -> service.filterByStatus("DONE"));
    }

    // ── getStatusSummary ─────────────────────────────────────────────────────
    @Test
    @DisplayName("Returns status summary map")
    void getStatusSummary() throws SQLException {
        when(dao.getStatusSummary()).thenReturn(Map.of("OPEN", 3, "RESOLVED", 1));

        Map<String, Integer> summary = service.getStatusSummary();
        assertEquals(3, summary.get("OPEN"));
        assertEquals(1, summary.get("RESOLVED"));
    }

    // ── helper ────────────────────────────────────────────────────────────────
    private Ticket makeTicket(long id, Status status) {
        Ticket t = new Ticket("Sample", "desc", "Alice", Priority.MEDIUM);
        t.setId(id);
        t.setStatus(status);
        return t;
    }
}
