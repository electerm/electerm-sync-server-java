package main.java.ElectermSync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataStore {

    private static final String DB_URL = "jdbc:h2:./electerm_sync_db";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2 Driver not found", e);
        }
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS user_data (" +
                         "user_id VARCHAR(255) PRIMARY KEY, " +
                         "data CLOB)";
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static WriteResult write(String jsonBody, String userId, Config dotenv) {
        String sql = "MERGE INTO user_data KEY(user_id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, jsonBody);
            pstmt.executeUpdate();

            return new WriteResult("ok", 200);

        } catch (SQLException e) {
            e.printStackTrace();
            return new WriteResult("Error writing to database", 500);
        }
    }

    public static ReadResult read(String userId, Config dotenv) {
        String sql = "SELECT data FROM user_data WHERE user_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String data = rs.getString("data");
                return new ReadResult(data, 200);
            } else {
                return new ReadResult("Data not found", 404);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ReadResult("Database read error", 500);
        }
    }
}