package database;

import java.sql.*;
import java.io.File;

/**
 * Singleton class for SQLite database connection management.
 * Handles connection pooling and database initialization.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private static final String DB_PATH = "data/resort_system.db";
    
    private DatabaseManager() {
        initializeDatabase();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Ensure data directory exists
                new File("data").mkdirs();
                String url = "jdbc:sqlite:" + DB_PATH;
                connection = DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    private void initializeDatabase() {
        Connection conn = getConnection();
        try (Statement stmt = conn.createStatement()) {
            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    email TEXT,
                    phone TEXT,
                    role TEXT DEFAULT 'customer',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Resorts table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS resorts (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    location TEXT NOT NULL,
                    description TEXT,
                    image_path TEXT
                )
            """);
            
            // Rooms table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    resort_id INTEGER NOT NULL,
                    room_type TEXT NOT NULL,
                    beds INTEGER NOT NULL,
                    price_per_night REAL NOT NULL,
                    available_count INTEGER NOT NULL,
                    FOREIGN KEY (resort_id) REFERENCES resorts(id)
                )
            """);
            
            // Food options table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS food_options (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    cuisine_type TEXT NOT NULL,
                    meal_plan TEXT NOT NULL,
                    price_per_day REAL NOT NULL
                )
            """);
            
            // Bookings table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bookings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    resort_id INTEGER NOT NULL,
                    room_id INTEGER NOT NULL,
                    food_option_id INTEGER,
                    check_in_date DATE NOT NULL,
                    check_out_date DATE NOT NULL,
                    num_guests INTEGER DEFAULT 1,
                    status TEXT DEFAULT 'confirmed',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (resort_id) REFERENCES resorts(id),
                    FOREIGN KEY (room_id) REFERENCES rooms(id),
                    FOREIGN KEY (food_option_id) REFERENCES food_options(id)
                )
            """);
            
            // Invoices table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS invoices (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    booking_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    invoice_number TEXT UNIQUE NOT NULL,
                    room_charges REAL NOT NULL,
                    food_charges REAL DEFAULT 0,
                    taxes REAL DEFAULT 0,
                    total_amount REAL NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (booking_id) REFERENCES bookings(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
            
            // Insert sample data if tables are empty
            insertSampleData(conn);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void insertSampleData(Connection conn) throws SQLException {
        // Check if users table is empty
        ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) == 0) {
            PreparedStatement ps;
            
            // Insert admin user (password: admin123)
            ps = conn.prepareStatement("INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)");
            ps.setString(1, "admin");
            ps.setString(2, "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"); // SHA-256 of "admin123"
            ps.setString(3, "admin@resort.com");
            ps.setString(4, "admin");
            ps.executeUpdate();
            
            // Insert sample resorts
            String[] resortData = {
                "Paradise Beach Resort|Maldives|A luxurious beachfront resort with crystal clear waters",
                "Mountain View Lodge|Switzerland|Alpine retreat with breathtaking mountain views",
                "Tropical Oasis Spa|Bali|Serene spa resort surrounded by lush tropical gardens"
            };
            
            ps = conn.prepareStatement("INSERT INTO resorts (name, location, description) VALUES (?, ?, ?)");
            for (String data : resortData) {
                String[] parts = data.split("\\|");
                ps.setString(1, parts[0]);
                ps.setString(2, parts[1]);
                ps.setString(3, parts[2]);
                ps.executeUpdate();
            }
            
            // Insert sample rooms for each resort
            String[][] roomData = {
                // Resort 1
                {"1", "Eco", "1", "100", "10"},
                {"1", "Premium", "2", "200", "8"},
                {"1", "Business", "3", "350", "5"},
                // Resort 2
                {"2", "Eco", "1", "120", "12"},
                {"2", "Premium", "2", "250", "6"},
                {"2", "Business", "4", "400", "4"},
                // Resort 3
                {"3", "Eco", "2", "150", "15"},
                {"3", "Premium", "2", "300", "7"},
                {"3", "Business", "3", "500", "3"}
            };
            
            ps = conn.prepareStatement("INSERT INTO rooms (resort_id, room_type, beds, price_per_night, available_count) VALUES (?, ?, ?, ?, ?)");
            for (String[] room : roomData) {
                ps.setInt(1, Integer.parseInt(room[0]));
                ps.setString(2, room[1]);
                ps.setInt(3, Integer.parseInt(room[2]));
                ps.setDouble(4, Double.parseDouble(room[3]));
                ps.setInt(5, Integer.parseInt(room[4]));
                ps.executeUpdate();
            }
            
            // Insert sample food options
            String[][] foodData = {
                {"Local", "Breakfast Only", "30"},
                {"Local", "Full Board", "80"},
                {"Italian", "Breakfast Only", "50"},
                {"Italian", "Full Board", "120"},
                {"Continental", "Breakfast Only", "70"},
                {"Continental", "Full Board", "150"}
            };
            
            ps = conn.prepareStatement("INSERT INTO food_options (cuisine_type, meal_plan, price_per_day) VALUES (?, ?, ?)");
            for (String[] food : foodData) {
                ps.setString(1, food[0]);
                ps.setString(2, food[1]);
                ps.setDouble(3, Double.parseDouble(food[2]));
                ps.executeUpdate();
            }
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
