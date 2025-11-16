package main.java.ElectermSync;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.ElectermSync.Config;
import main.java.ElectermSync.DataStore;
import main.java.ElectermSync.ReadResult;
import main.java.ElectermSync.WriteResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataStoreTest {
    private final Config dotenv = new Config();
    private final String userId = "testUserId";

    @BeforeEach
    public void setup() {
        // Clean up test data
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./electerm_sync_db", "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_data WHERE user_id = '" + userId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up test data
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./electerm_sync_db", "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_data WHERE user_id = '" + userId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWriteAndRead() {
        String jsonBody = "{\"key\": \"value\"}";

        WriteResult writeResult = DataStore.write(jsonBody, userId, dotenv);
        assertEquals(200, writeResult.statusCode);
        assertEquals("ok", writeResult.message);

        ReadResult readResult = DataStore.read(userId, dotenv);
        assertEquals(200, readResult.statusCode);
        assertEquals(jsonBody, readResult.fileData);
    }

    @Test
    public void testInvalidRead() {
        ReadResult readResult = DataStore.read(userId + "xx", dotenv);
        assertEquals(404, readResult.statusCode);
        assertEquals("Data not found", readResult.fileData);
    }

    @Test
    public void testOverwriteData() {
        String jsonBody1 = "{\"key\": \"value1\"}";
        String jsonBody2 = "{\"key\": \"value2\"}";

        WriteResult writeResult1 = DataStore.write(jsonBody1, userId, dotenv);
        assertEquals(200, writeResult1.statusCode);
        assertEquals("ok", writeResult1.message);

        WriteResult writeResult2 = DataStore.write(jsonBody2, userId, dotenv);
        assertEquals(200, writeResult2.statusCode);
        assertEquals("ok", writeResult2.message);

        ReadResult readResult = DataStore.read(userId, dotenv);
        assertEquals(200, readResult.statusCode);
        assertEquals(jsonBody2, readResult.fileData); // Should be the latest
    }

    @Test
    public void testEmptyData() {
        String jsonBody = "{}";

        WriteResult writeResult = DataStore.write(jsonBody, userId, dotenv);
        assertEquals(200, writeResult.statusCode);
        assertEquals("ok", writeResult.message);

        ReadResult readResult = DataStore.read(userId, dotenv);
        assertEquals(200, readResult.statusCode);
        assertEquals(jsonBody, readResult.fileData);
    }

    @Test
    public void testLargeData() {
        StringBuilder largeJson = new StringBuilder("{\"data\": \"");
        for (int i = 0; i < 10000; i++) {
            largeJson.append("a");
        }
        largeJson.append("\"}");

        WriteResult writeResult = DataStore.write(largeJson.toString(), userId, dotenv);
        assertEquals(200, writeResult.statusCode);
        assertEquals("ok", writeResult.message);

        ReadResult readResult = DataStore.read(userId, dotenv);
        assertEquals(200, readResult.statusCode);
        assertEquals(largeJson.toString(), readResult.fileData);
    }
}
