package dao;

import database.DatabaseManager;
import models.Resort;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Resort operations.
 */
public class ResortDAO {
    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new resort.
     */
    public boolean create(Resort resort) {
        String sql = "INSERT INTO resorts (name, location, description, image_path) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, resort.getName());
            ps.setString(2, resort.getLocation());
            ps.setString(3, resort.getDescription());
            ps.setString(4, resort.getImagePath());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    resort.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update an existing resort.
     */
    public boolean update(Resort resort) {
        String sql = "UPDATE resorts SET name = ?, location = ?, description = ?, image_path = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, resort.getName());
            ps.setString(2, resort.getLocation());
            ps.setString(3, resort.getDescription());
            ps.setString(4, resort.getImagePath());
            ps.setInt(5, resort.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a resort by ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM resorts WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find resort by ID.
     */
    public Resort findById(int id) {
        String sql = "SELECT * FROM resorts WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToResort(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all resorts.
     */
    public List<Resort> getAll() {
        List<Resort> resorts = new ArrayList<>();
        String sql = "SELECT * FROM resorts ORDER BY name";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resorts.add(mapResultSetToResort(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resorts;
    }

    private Resort mapResultSetToResort(ResultSet rs) throws SQLException {
        Resort resort = new Resort();
        resort.setId(rs.getInt("id"));
        resort.setName(rs.getString("name"));
        resort.setLocation(rs.getString("location"));
        resort.setDescription(rs.getString("description"));
        resort.setImagePath(rs.getString("image_path"));
        return resort;
    }
}
