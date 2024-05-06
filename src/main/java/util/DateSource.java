package util;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

;

public class DateSource {
    private static final String URL = "jdbc:mysql://localhost:3306/ecopartage1";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connection established");
            } catch (SQLException e) {
                System.err.println("Failed to establish connection: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed");
        }
    }

    public static DateSource getInstance() {
        return new DateSource();
    }
}
