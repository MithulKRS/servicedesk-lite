package com.servicedesk.dao;

import com.servicedesk.model.Ticket;
import com.servicedesk.model.Ticket.Priority;
import com.servicedesk.model.Ticket.Status;
import com.servicedesk.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TicketDAO {

    // ── INSERT ────────────────────────────────────────────────────────────────
    public long createTicket(Ticket t) throws SQLException {
        String sql = "INSERT INTO tickets (title, description, requester, priority, status) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, t.getTitle());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getRequester());
            ps.setString(4, t.getPriority().name());
            ps.setString(5, t.getStatus().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Insert failed – no generated key returned.");
    }

    // ── SELECT ALL ────────────────────────────────────────────────────────────
    public List<Ticket> findAll() throws SQLException {
        return query("SELECT * FROM tickets ORDER BY id DESC", ps -> {});
    }

    // ── SELECT BY ID ──────────────────────────────────────────────────────────
    public Ticket findById(long id) throws SQLException {
        List<Ticket> results = query(
                "SELECT * FROM tickets WHERE id = ?",
                ps -> ps.setLong(1, id));
        return results.isEmpty() ? null : results.get(0);
    }

    // ── FILTER BY STATUS ──────────────────────────────────────────────────────
    public List<Ticket> findByStatus(Status status) throws SQLException {
        return query("SELECT * FROM tickets WHERE status = ? ORDER BY id DESC",
                ps -> ps.setString(1, status.name()));
    }

    // ── FILTER BY PRIORITY ────────────────────────────────────────────────────
    public List<Ticket> findByPriority(Priority priority) throws SQLException {
        return query("SELECT * FROM tickets WHERE priority = ? ORDER BY id DESC",
                ps -> ps.setString(1, priority.name()));
    }

    // ── UPDATE STATUS ─────────────────────────────────────────────────────────
    public boolean updateStatus(long id, Status status) throws SQLException {
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── SUMMARY (GROUP BY) ────────────────────────────────────────────────────
    public Map<String, Integer> getStatusSummary() throws SQLException {
        String sql = "SELECT status, COUNT(*) AS cnt FROM tickets GROUP BY status ORDER BY status";
        Map<String, Integer> summary = new LinkedHashMap<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                summary.put(rs.getString("status"), rs.getInt("cnt"));
            }
        }
        return summary;
    }

    // ── INTERNAL HELPER ───────────────────────────────────────────────────────
    @FunctionalInterface
    private interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Ticket> query(String sql, Binder binder) throws SQLException {
        List<Ticket> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Ticket map(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getLong("id"));
        t.setTitle(rs.getString("title"));
        t.setDescription(rs.getString("description"));
        t.setRequester(rs.getString("requester"));
        t.setPriority(Priority.valueOf(rs.getString("priority")));
        t.setStatus(Status.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) t.setCreatedAt(ts.toLocalDateTime());
        return t;
    }
}
