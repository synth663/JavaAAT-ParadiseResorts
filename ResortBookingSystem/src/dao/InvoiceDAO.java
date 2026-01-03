package dao;

import database.DatabaseManager;
import models.Invoice;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Invoice operations.
 */
public class InvoiceDAO {
    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new invoice.
     */
    public boolean create(Invoice invoice) {
        String sql = "INSERT INTO invoices (booking_id, user_id, invoice_number, room_charges, food_charges, taxes, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, invoice.getBookingId());
            ps.setInt(2, invoice.getUserId());
            ps.setString(3, invoice.getInvoiceNumber());
            ps.setDouble(4, invoice.getRoomCharges());
            ps.setDouble(5, invoice.getFoodCharges());
            ps.setDouble(6, invoice.getTaxes());
            ps.setDouble(7, invoice.getTotalAmount());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    invoice.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find invoices by user ID.
     */
    public List<Invoice> findByUser(int userId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = """
                    SELECT i.*, u.username, res.name as resort_name
                    FROM invoices i
                    JOIN users u ON i.user_id = u.id
                    JOIN bookings b ON i.booking_id = b.id
                    JOIN resorts res ON b.resort_id = res.id
                    WHERE i.user_id = ?
                    ORDER BY i.created_at DESC
                """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    /**
     * Find invoice by booking ID.
     */
    public Invoice findByBooking(int bookingId) {
        String sql = """
                    SELECT i.*, u.username, res.name as resort_name
                    FROM invoices i
                    JOIN users u ON i.user_id = u.id
                    JOIN bookings b ON i.booking_id = b.id
                    JOIN resorts res ON b.resort_id = res.id
                    WHERE i.booking_id = ?
                """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToInvoice(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all invoices.
     */
    public List<Invoice> getAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = """
                    SELECT i.*, u.username, res.name as resort_name
                    FROM invoices i
                    JOIN users u ON i.user_id = u.id
                    JOIN bookings b ON i.booking_id = b.id
                    JOIN resorts res ON b.resort_id = res.id
                    ORDER BY i.created_at DESC
                """;
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("id"));
        invoice.setBookingId(rs.getInt("booking_id"));
        invoice.setUserId(rs.getInt("user_id"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setRoomCharges(rs.getDouble("room_charges"));
        invoice.setFoodCharges(rs.getDouble("food_charges"));
        invoice.setTaxes(rs.getDouble("taxes"));
        invoice.setTotalAmount(rs.getDouble("total_amount"));

        String createdAt = rs.getString("created_at");
        if (createdAt != null) {
            invoice.setCreatedAt(LocalDateTime.parse(createdAt.replace(" ", "T")));
        }

        try {
            invoice.setUsername(rs.getString("username"));
            invoice.setResortName(rs.getString("resort_name"));
        } catch (SQLException e) {
            // fields not in query
        }

        return invoice;
    }
}
