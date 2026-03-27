package database;

import model.FoodEntry;
import model.UserProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager handles all JDBC connections and SQL operations
 * for the PHFMS local MySQL database (health_db).
 * Implements the Singleton pattern so only one connection is active.
 */
public class DatabaseManager {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/health_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password"; // change for your setup

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Database connected successfully.");
            createTablesIfNotExist();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    /** Returns the single shared instance (Singleton). */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // ----------------------------------------------------------------
    // Schema creation
    // ----------------------------------------------------------------

    private void createTablesIfNotExist() throws SQLException {
        String createProfile = """
            CREATE TABLE IF NOT EXISTS user_profile (
                id       INT AUTO_INCREMENT PRIMARY KEY,
                name     VARCHAR(100),
                weight   DOUBLE,
                height   DOUBLE,
                age      INT,
                gender   VARCHAR(10)
            )""";

        String createFood = """
            CREATE TABLE IF NOT EXISTS food_log (
                id               INT AUTO_INCREMENT PRIMARY KEY,
                food_name        VARCHAR(100),
                quantity_grams   DOUBLE,
                calories_per_gram DOUBLE,
                protein_per_gram DOUBLE,
                log_date         DATE DEFAULT (CURRENT_DATE)
            )""";

        String createExercise = """
            CREATE TABLE IF NOT EXISTS exercise_log (
                id               INT AUTO_INCREMENT PRIMARY KEY,
                exercise_name    VARCHAR(100),
                exercise_type    VARCHAR(20),
                duration_minutes INT,
                calories_burned  DOUBLE,
                log_date         DATE DEFAULT (CURRENT_DATE)
            )""";

        String createFoodRef = """
            CREATE TABLE IF NOT EXISTS food_reference (
                id                INT AUTO_INCREMENT PRIMARY KEY,
                food_name         VARCHAR(100) UNIQUE,
                calories_per_gram DOUBLE,
                protein_per_gram  DOUBLE
            )""";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createProfile);
            stmt.execute(createFood);
            stmt.execute(createExercise);
            stmt.execute(createFoodRef);
            seedFoodReference();
        }
    }

    /** Inserts common foods into the reference table if it's empty. */
    private void seedFoodReference() throws SQLException {
        String check = "SELECT COUNT(*) FROM food_reference";
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(check)) {
            rs.next();
            if (rs.getInt(1) > 0) return; // already seeded
        }

        String insert = "INSERT INTO food_reference (food_name, calories_per_gram, protein_per_gram) VALUES (?,?,?)";
        Object[][] foods = {
            {"Chicken Breast", 1.65, 0.31},
            {"Brown Rice",     1.30, 0.026},
            {"Banana",         0.89, 0.011},
            {"Egg",            1.55, 0.13},
            {"Oats",           3.89, 0.17},
            {"Broccoli",       0.34, 0.028},
            {"Salmon",         2.08, 0.20},
            {"Apple",          0.52, 0.003},
            {"Greek Yogurt",   0.59, 0.10},
            {"Whole Milk",     0.61, 0.032}
        };

        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            for (Object[] food : foods) {
                ps.setString(1, (String)  food[0]);
                ps.setDouble(2, (Double)  food[1]);
                ps.setDouble(3, (Double)  food[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ----------------------------------------------------------------
    // User Profile operations
    // ----------------------------------------------------------------

    public void saveUserProfile(UserProfile profile) {
        String delete = "DELETE FROM user_profile"; // single-user app
        String insert = "INSERT INTO user_profile (name, weight, height, age, gender) VALUES (?,?,?,?,?)";
        try (Statement del  = connection.createStatement();
             PreparedStatement ps = connection.prepareStatement(insert)) {
            del.execute(delete);
            ps.setString(1, profile.getName());
            ps.setDouble(2, profile.getWeight());
            ps.setDouble(3, profile.getHeight());
            ps.setInt   (4, profile.getAge());
            ps.setString(5, profile.getGender());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving profile: " + e.getMessage());
        }
    }

    public UserProfile loadUserProfile() {
        String query = "SELECT * FROM user_profile LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(query)) {
            if (rs.next()) {
                return new UserProfile(
                    rs.getString("name"),
                    rs.getDouble("weight"),
                    rs.getDouble("height"),
                    rs.getInt   ("age"),
                    rs.getString("gender")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error loading profile: " + e.getMessage());
        }
        return null;
    }

    // ----------------------------------------------------------------
    // Food log operations
    // ----------------------------------------------------------------

    public void saveFoodEntry(FoodEntry entry) {
        String insert = """
            INSERT INTO food_log (food_name, quantity_grams, calories_per_gram, protein_per_gram)
            VALUES (?,?,?,?)""";
        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            ps.setString(1, entry.getFoodName());
            ps.setDouble(2, entry.getQuantityGrams());
            ps.setDouble(3, entry.getCaloriesPerGram());
            ps.setDouble(4, entry.getProteinPerGram());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving food entry: " + e.getMessage());
        }
    }

    public List<FoodEntry> loadTodayFoodLog() {
        List<FoodEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM food_log WHERE log_date = CURRENT_DATE";
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(query)) {
            while (rs.next()) {
                entries.add(new FoodEntry(
                    rs.getString("food_name"),
                    rs.getDouble("quantity_grams"),
                    rs.getDouble("calories_per_gram"),
                    rs.getDouble("protein_per_gram")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading food log: " + e.getMessage());
        }
        return entries;
    }

    // ----------------------------------------------------------------
    // Exercise log operations
    // ----------------------------------------------------------------

    public void saveExerciseEntry(String name, String type, int durationMinutes, double caloriesBurned) {
        String insert = """
            INSERT INTO exercise_log (exercise_name, exercise_type, duration_minutes, calories_burned)
            VALUES (?,?,?,?)""";
        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setInt   (3, durationMinutes);
            ps.setDouble(4, caloriesBurned);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving exercise: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Food reference lookup
    // ----------------------------------------------------------------

    public double[] lookupFood(String foodName) {
        String query = "SELECT calories_per_gram, protein_per_gram FROM food_reference WHERE food_name = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, foodName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new double[]{ rs.getDouble(1), rs.getDouble(2) };
            }
        } catch (SQLException e) {
            System.err.println("Error looking up food: " + e.getMessage());
        }
        return null;
    }

    public List<String> getAllFoodNames() {
        List<String> names = new ArrayList<>();
        String query = "SELECT food_name FROM food_reference ORDER BY food_name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(query)) {
            while (rs.next()) names.add(rs.getString("food_name"));
        } catch (SQLException e) {
            System.err.println("Error fetching food names: " + e.getMessage());
        }
        return names;
    }

    public Connection getConnection() { return connection; }
}
