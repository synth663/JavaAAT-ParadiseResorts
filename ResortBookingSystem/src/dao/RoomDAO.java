package dao;

import database.DatabaseManager;
import models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Room operations.
 */
public class RoomDAO {
    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new room.
     */
    public boolean create(Room room) {
        String sql = "INSERT INTO rooms (resort_id, room_type, beds, price_per_night, available_count) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, room.getResortId());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getBeds());
            ps.setDouble(4, room.getPricePerNight());
            ps.setInt(5, room.getAvailableCount());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    room.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update an existing room.
     */
    public boolean update(Room room) {
        String sql = "UPDATE rooms SET resort_id = ?, room_type = ?, beds = ?, price_per_night = ?, available_count = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, room.getResortId());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getBeds());
            ps.setDouble(4, room.getPricePerNight());
            ps.setInt(5, room.getAvailableCount());
            ps.setInt(6, room.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a room by ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find room by ID.
     */
    public Room findById(int id) {
        String sql = "SELECT r.*, res.name as resort_name FROM rooms r JOIN resorts res ON r.resort_id = res.id WHERE r.id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get rooms by resort ID.
     */
    public List<Room> findByResort(int resortId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, res.name as resort_name FROM rooms r JOIN resorts res ON r.resort_id = res.id WHERE r.resort_id = ? ORDER BY r.room_type";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, resortId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get available rooms (with count > 0).
     */
    public List<Room> getAvailable(int resortId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, res.name as resort_name FROM rooms r JOIN resorts res ON r.resort_id = res.id WHERE r.resort_id = ? AND r.available_count > 0 ORDER BY r.price_per_night";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, resortId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get all rooms.
     */
    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT r.*, res.name as resort_name FROM rooms r JOIN resorts res ON r.resort_id = res.id ORDER BY res.name, r.room_type";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Update room availability.
     */
    public boolean updateAvailability(int roomId, int delta) {
        String sql = "UPDATE rooms SET available_count = available_count + ? WHERE id = ? AND available_count + ? >= 0";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, roomId);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setResortId(rs.getInt("resort_id"));
        room.setRoomType(rs.getString("room_type"));
        room.setBeds(rs.getInt("beds"));
        room.setPricePerNight(rs.getDouble("price_per_night"));
        room.setAvailableCount(rs.getInt("available_count"));
        try {
            room.setResortName(rs.getString("resort_name"));
        } catch (SQLException e) {
            // resort_name not in query
        }
        return room;
    }
}
