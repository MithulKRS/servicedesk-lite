package com.servicedesk;

import com.servicedesk.dao.TicketDAO;
import com.servicedesk.model.Ticket;
import com.servicedesk.service.TicketService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final TicketService service = new TicketService(new TicketDAO());
    private static final Scanner       scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      ServiceDesk Lite v1.0       ║");
        System.out.println("╚══════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();
            try {
                switch (choice) {
                    case "1" -> createTicket();
                    case "2" -> listAllTickets();
                    case "3" -> searchById();
                    case "4" -> updateStatus();
                    case "5" -> filterTickets();
                    case "6" -> showSummary();
                    case "0" -> { running = false; System.out.println("Goodbye!"); }
                    default  -> System.out.println("Invalid option. Try again.");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("⚠  " + ex.getMessage());
            } catch (SQLException ex) {
                System.out.println("✗  Database error: " + ex.getMessage());
            }
            System.out.println();
        }
    }

    // ── MENU ──────────────────────────────────────────────────────────────────
    private static void printMenu() {
        System.out.println("─────────────────────────────────");
        System.out.println(" 1. Create ticket");
        System.out.println(" 2. View all tickets");
        System.out.println(" 3. Search by ID");
        System.out.println(" 4. Update ticket status");
        System.out.println(" 5. Filter tickets");
        System.out.println(" 6. Status summary report");
        System.out.println(" 0. Exit");
        System.out.print("Choose: ");
    }

    // ── ACTIONS ───────────────────────────────────────────────────────────────
    private static void createTicket() throws SQLException {
        System.out.print("Title       : "); String title       = scanner.nextLine().trim();
        System.out.print("Description : "); String description = scanner.nextLine().trim();
        System.out.print("Requester   : "); String requester   = scanner.nextLine().trim();
        System.out.print("Priority (LOW/MEDIUM/HIGH): "); String priority = scanner.nextLine().trim();

        Ticket t = service.createTicket(title, description, requester, priority);
        System.out.println("✓ Ticket created: " + t);
    }

    private static void listAllTickets() throws SQLException {
        List<Ticket> tickets = service.listAll();
        if (tickets.isEmpty()) {
            System.out.println("No tickets found.");
            return;
        }
        printHeader();
        tickets.forEach(System.out::println);
    }

    private static void searchById() throws SQLException {
        System.out.print("Enter ticket ID: ");
        long id = parseLong(scanner.nextLine().trim());
        Ticket t = service.findById(id);
        printHeader();
        System.out.println(t);
        if (t.getDescription() != null && !t.getDescription().isBlank())
            System.out.println("  Description: " + t.getDescription());
    }

    private static void updateStatus() throws SQLException {
        System.out.print("Ticket ID : "); long id = parseLong(scanner.nextLine().trim());
        System.out.print("New status (OPEN/IN_PROGRESS/RESOLVED): ");
        String status = scanner.nextLine().trim();
        service.updateStatus(id, status);
        System.out.println("✓ Status updated.");
    }

    private static void filterTickets() throws SQLException {
        System.out.println("Filter by:");
        System.out.println("  1. Status   (OPEN / IN_PROGRESS / RESOLVED)");
        System.out.println("  2. Priority (LOW / MEDIUM / HIGH)");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();

        List<Ticket> tickets;
        if ("1".equals(choice)) {
            System.out.print("Status: ");
            tickets = service.filterByStatus(scanner.nextLine().trim());
        } else if ("2".equals(choice)) {
            System.out.print("Priority: ");
            tickets = service.filterByPriority(scanner.nextLine().trim());
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        if (tickets.isEmpty()) { System.out.println("No tickets match."); return; }
        printHeader();
        tickets.forEach(System.out::println);
    }

    private static void showSummary() throws SQLException {
        Map<String, Integer> summary = service.getStatusSummary();
        System.out.println("── Ticket Status Summary ──");
        int total = 0;
        for (Map.Entry<String, Integer> e : summary.entrySet()) {
            System.out.printf("  %-15s : %d%n", e.getKey(), e.getValue());
            total += e.getValue();
        }
        System.out.println("  ─────────────────────────");
        System.out.printf("  %-15s : %d%n", "TOTAL", total);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────
    private static void printHeader() {
        System.out.printf("%-6s %-40s %-8s %-11s %-20s %s%n",
                "ID", "TITLE", "PRIORITY", "STATUS", "REQUESTER", "CREATED");
        System.out.println("─".repeat(100));
    }

    private static long parseLong(String s) {
        try { return Long.parseLong(s); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + s + "' is not a valid ID.");
        }
    }
}
