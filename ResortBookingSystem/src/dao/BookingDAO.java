package dao;

import database.DatabaseManager;
import models.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking operations.
 */
public class BookingDAO {
    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new booking.
     */
    public boolean create(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, resort_id, room_id, food_option_id, check_in_date, check_out_date, num_guests, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getResortId());
            ps.setInt(3, booking.getRoomId());
            if (booking.getFoodOptionId() != null) {
                ps.setInt(4, booking.getFoodOptionId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, booking.getCheckInDate().toString());
            ps.setString(6, booking.getCheckOutDate().toString());
            ps.setInt(7, booking.getNumGuests());
            ps.setString(8, booking.getStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    booking.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find bookings by user ID.
     */
    public List<Booking> findByUser(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
                    SELECT b.*, res.name as resort_name, r.room_type, r.beds, f.cuisine_type, f.meal_plan, u.username
                    FROM bookings b
                    JOIN resorts res ON b.resort_id = res.id
                    JOIN rooms r ON b.room_id = r.id
                    LEFT JOIN food_options f ON b.food_option_id = f.id
                    JOIN users u ON b.user_id = u.id
                    WHERE b.user_id = ?
                    ORDER BY b.created_at DESC
                """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Get all bookings.
     */
    public List<Booking> getAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
                    SELECT b.*, res.name as resort_name, r.room_type, r.beds, f.cuisine_type, f.meal_plan, u.username
                    FROM bookings b
                    JOIN resorts res ON b.resort_id = res.id
                    JOIN rooms r ON b.room_id = r.id
                    LEFT JOIN food_options f ON b.food_option_id = f.id
                    JOIN users u ON b.user_id = u.id
                    ORDER BY b.created_at DESC
                """;
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    /**
     * Find booking by ID.
     */
    public Booking findById(int id) {
        String sql = """
                    SELECT b.*, res.name as resort_name, r.room_type, r.beds, f.cuisine_type, f.meal_plan, u.username
                    FROM bookings b
                    JOIN resorts res ON b.resort_id = res.id
                    JOIN rooms r ON b.room_id = r.id
                    LEFT JOIN food_options f ON b.food_option_id = f.id
                    JOIN users u ON b.user_id = u.id
                    WHERE b.id = ?
                """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToBooking(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update booking status.
     */
    public boolean updateStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setResortId(rs.getInt("resort_id"));
        booking.setRoomId(rs.getInt("room_id"));
        int foodOptionId = rs.getInt("food_option_id");
        if (!rs.wasNull()) {
            booking.setFoodOptionId(foodOptionId);
        }
        booking.setCheckInDate(LocalDate.parse(rs.getString("check_in_date")));
        booking.setCheckOutDate(LocalDate.parse(rs.getString("check_out_date")));
        booking.setNumGuests(rs.getInt("num_guests"));
        booking.setStatus(rs.getString("status"));

        String createdAt = rs.getString("created_at");
        if (createdAt != null) {
            booking.setCreatedAt(LocalDateTime.parse(createdAt.replace(" ", "T")));
        }

        try {
            booking.setResortName(rs.getString("resort_name"));
            booking.setRoomType(rs.getString("room_type"));
            booking.setBeds(rs.getInt("beds"));
            booking.setCuisineType(rs.getString("cuisine_type"));
            booking.setMealPlan(rs.getString("meal_plan"));
            booking.setUsername(rs.getString("username"));
        } catch (SQLException e) {
            // fields not in query
        }

        return booking;
    }
}
