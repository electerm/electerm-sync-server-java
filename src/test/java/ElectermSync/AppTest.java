package main.java.ElectermSync;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.restassured.RestAssured;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppTest {

    private static final String TEST_HOST = "127.0.0.1";
    private static final int TEST_PORT = 7838; // Different from default
    private static final String JWT_SECRET = "testSecret283ert544trggger283ert544trggger283ert544trggger283ert544trggger283ert544trggger283ert544trggger";
    private static final String VALID_USER = "testUser";
    private static final String INVALID_USER = "invalidUser";

    private static Thread serverThread;

    @BeforeAll
    public static void setupServer() throws InterruptedException {
        // Clean up test data
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./electerm_sync_db", "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_data WHERE user_id = '" + VALID_USER + "'");
        } catch (SQLException e) {
            // Ignore if table doesn't exist yet
        }

        // Set system properties for test
        System.setProperty("JWT_SECRET", JWT_SECRET);
        System.setProperty("JWT_USERS", VALID_USER);
        System.setProperty("PORT", String.valueOf(TEST_PORT));
        System.setProperty("HOST", TEST_HOST);

        // Start server in a separate thread
        serverThread = new Thread(() -> {
            try {
                App.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Wait for server to start
        TimeUnit.SECONDS.sleep(2);

        RestAssured.baseURI = "http://" + TEST_HOST;
        RestAssured.port = TEST_PORT;
    }

    @BeforeEach
    public void setupEach() {
        // Clean up test data before each test
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./electerm_sync_db", "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_data WHERE user_id = '" + VALID_USER + "'");
        } catch (SQLException e) {
            // Ignore
        }
    }

    @AfterAll
    public static void tearDownServer() {
        // Clean up test data
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./electerm_sync_db", "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM user_data WHERE user_id = '" + VALID_USER + "'");
        } catch (SQLException e) {
            // Ignore
        }

        if (serverThread != null) {
            serverThread.interrupt();
        }
        // Note: Spark doesn't have a clean shutdown, but for tests it's ok
    }

    private String generateToken(String userId) {
        byte[] bytesToEncode = JWT_SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String secret = Base64.getEncoder().encodeToString(bytesToEncode);

        return Jwts.builder()
                .setSubject(userId)
                .claim("id", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    @Test
    public void testGetWithoutAuth() {
        given()
        .when()
            .get("/api/sync")
        .then()
            .statusCode(401);
    }

    @Test
    public void testGetWithInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        given()
            .header("Authorization", "Bearer " + invalidToken)
        .when()
            .get("/api/sync")
        .then()
            .statusCode(401);
    }

    @Test
    public void testGetWithInvalidUser() {
        String token = generateToken(INVALID_USER);

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/sync")
        .then()
            .statusCode(401);
    }

    @Test
    public void testGetNoData() {
        String token = generateToken(VALID_USER);

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/sync")
        .then()
            .statusCode(404)
            .body(equalTo("Data not found"));
    }

    @Test
    public void testPutAndGet() {
        String token = generateToken(VALID_USER);
        String jsonBody = "{\"key\": \"testValue\"}";

        // PUT data
        given()
            .header("Authorization", "Bearer " + token)
            .contentType("application/json")
            .body(jsonBody)
        .when()
            .put("/api/sync")
        .then()
            .statusCode(200)
            .body(equalTo("ok"));

        // GET data
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/api/sync")
        .then()
            .statusCode(200)
            .body(equalTo(jsonBody));
    }

    @Test
    public void testPost() {
        String token = generateToken(VALID_USER);

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .post("/api/sync")
        .then()
            .statusCode(200)
            .body(equalTo("test ok"));
    }
}