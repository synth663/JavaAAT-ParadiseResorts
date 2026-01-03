package dao;

import database.DatabaseManager;
import models.FoodOption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for FoodOption operations.
 */
public class FoodDAO {
    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    /**
     * Create a new food option.
     */
    public boolean create(FoodOption food) {
        String sql = "INSERT INTO food_options (cuisine_type, meal_plan, price_per_day) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, food.getCuisineType());
            ps.setString(2, food.getMealPlan());
            ps.setDouble(3, food.getPricePerDay());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    food.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update an existing food option.
     */
    public boolean update(FoodOption food) {
        String sql = "UPDATE food_options SET cuisine_type = ?, meal_plan = ?, price_per_day = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, food.getCuisineType());
            ps.setString(2, food.getMealPlan());
            ps.setDouble(3, food.getPricePerDay());
            ps.setInt(4, food.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a food option by ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM food_options WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find food option by ID.
     */
    public FoodOption findById(int id) {
        String sql = "SELECT * FROM food_options WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToFoodOption(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all food options.
     */
    public List<FoodOption> getAll() {
        List<FoodOption> options = new ArrayList<>();
        String sql = "SELECT * FROM food_options ORDER BY cuisine_type, meal_plan";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                options.add(mapResultSetToFoodOption(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }

    private FoodOption mapResultSetToFoodOption(ResultSet rs) throws SQLException {
        FoodOption food = new FoodOption();
        food.setId(rs.getInt("id"));
        food.setCuisineType(rs.getString("cuisine_type"));
        food.setMealPlan(rs.getString("meal_plan"));
        food.setPricePerDay(rs.getDouble("price_per_day"));
        return food;
    }
}
