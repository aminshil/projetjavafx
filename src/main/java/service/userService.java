package service;

import entite.user;
import jakarta.mail.MessagingException;
import util.DateSource;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.EmailUtil;

public class userService {
    private Connection conn;

    // Constructor that establishes a database connection
    public userService() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/ecopartage1";
        String user = "root";
        String password = "";
        conn = DriverManager.getConnection(url, user, password);
    }

    // Method to add a user to the database
    public void addUser(user user) throws SQLException, IOException {
        String sql = "INSERT INTO user (nom, prenom, email, password, role, dateNaissance, imagePath, isBlocked) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());
            pstmt.setDate(6, Date.valueOf(user.getDateNaissance()));
            pstmt.setString(7, user.getImagePath());
            pstmt.setBoolean(8, user.isBlocked()); // Set the value for 'isBlocked'
            pstmt.executeUpdate();
        }
    }
    // Method to delete a user from the database
    public void deleteUser(user user) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        }
    }

    // Method to retrieve all users from the database
    public List<user> getAllUsers() throws SQLException {
        List<user> userList = new ArrayList<>();
        String query = "SELECT id, nom, prenom, email, password, role, dateNaissance, imagePath, isBlocked FROM user";
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                LocalDate dateNaissance = resultSet.getDate("dateNaissance") != null ? resultSet.getDate("dateNaissance").toLocalDate() : null;
                String imagePath = resultSet.getString("imagePath");
                boolean isBlocked = resultSet.getBoolean("isBlocked");

                user user = new user(id, nom, prenom, email, password, role, dateNaissance, imagePath, isBlocked);
                userList.add(user);
            }
        }
        return userList;
    }

    // Method to update a user's details in the database
    public void updateUser(user user) throws SQLException {
        String sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, password = ?, role = ?, dateNaissance = ?, imagePath = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());
            pstmt.setDate(6, Date.valueOf(user.getDateNaissance()));
            pstmt.setString(7, user.getImagePath());
            pstmt.setInt(8, user.getId());
            pstmt.executeUpdate();
        }
    }

    // Method to authenticate a user by email and password
    public user loginUser(String email, String password) throws SQLException {
        String query = "SELECT id, nom, prenom, role, dateNaissance, imagePath, isBlocked FROM user WHERE email = ? AND password = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int userId = resultSet.getInt("id");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    String role = resultSet.getString("role");
                    LocalDate dateNaissance = resultSet.getDate("dateNaissance") != null ? resultSet.getDate("dateNaissance").toLocalDate() : null;
                    String imagePath = resultSet.getString("imagePath");
                    boolean isBlocked = resultSet.getBoolean("isBlocked");

                    return new user(userId, nom, prenom, email, password, role, dateNaissance, imagePath, isBlocked);
                }
            }
        }


        return null;

    }

    public void toggleUserBlock(int userId, boolean block) throws SQLException, MessagingException {
        String sql = "UPDATE user SET isBlocked = ?, blockEndTime = ? WHERE id = ?";
        try (Connection conn = DateSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, block);
            stmt.setTimestamp(2, block ? Timestamp.valueOf(LocalDateTime.now().plusDays(30)) : null);
            stmt.setInt(3, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0 && block) {
                sendBlockNotification(userId); // Handle email notification separately
            }
        } catch (SQLException | MessagingException e) {
            e.printStackTrace();
            throw e;
        }
    }
    private void sendBlockNotification(int userId) throws SQLException, MessagingException {
        String userEmail = retrieveUserEmailById(userId);
        if (userEmail != null) {
            // Retrieve email configuration from environment variables
            String emailHost = "smtp.office365.com";
            String emailPort = "587";
            String emailUser = "EcopartageJava@outlook.fr";
            String emailPass = "Houssem10";
            String subject = "Account Blocked";
            String content = "Your account has been blocked due to policy violations. It will be automatically unblocked after 30 days.";

            // Ensure that all necessary details are available
            if (emailHost != null && emailPort != null && emailUser != null && emailPass != null) {
                EmailUtil.sendEmail(emailHost, emailPort, emailUser, emailPass, userEmail, subject, content);
            } else {
                System.out.println("Email configuration is incomplete. Please check environment settings.");
            }
        }
    }


    private String retrieveUserEmailById(int userId) throws SQLException {
        String userEmail = null;
        String query = "SELECT email FROM user WHERE id = ?";
        try (Connection connection = DateSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    userEmail = resultSet.getString("email");
                }
            }
        }
        return userEmail;
    }
    public int countVerifiedUsers(boolean isVerified) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE isVerified = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setBoolean(1, isVerified);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

    public user getUserById(int userId) throws SQLException {
        String query = "SELECT id, nom, prenom, email, password, role, dateNaissance, imagePath, isBlocked FROM user WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nom = resultSet.getString("nom");
                    String prenom = resultSet.getString("prenom");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    String role = resultSet.getString("role");
                    LocalDate dateNaissance = resultSet.getDate("dateNaissance") != null ? resultSet.getDate("dateNaissance").toLocalDate() : null;
                    String imagePath = resultSet.getString("imagePath");
                    boolean isBlocked = resultSet.getBoolean("isBlocked");

                    return new user(id, nom, prenom, email, password, role, dateNaissance, imagePath, isBlocked);
                }
            }
        }
        return null;
    }



}


