package sample;

import entite.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.DateSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import sample.SessionManager;
import service.userService;

public class LoginController {

    @FXML
    private TextField tf_email;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Label lbl_status;

    @FXML
    private Button button_signup;

    @FXML
    private void login() {
        String email = tf_email.getText().trim();
        String password = tf_password.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            lbl_status.setText("Email and password are required.");
            return;
        }

        try {
            userService service = new userService();
            user loggedInUser = service.loginUser(email, hashPassword(password));

            if (loggedInUser != null) {
                if (loggedInUser.isBlocked()) {
                    lbl_status.setText("Votre compte est bloqué");
                    return; // Prevent blocked users from logging in
                }

                SessionManager.setCurrentUser(loggedInUser); // Set the current user

                // Update session with all fields of the user
                SessionManager.setCurrentUserId(loggedInUser.getId());
                SessionManager.setCurrentUserNom(loggedInUser.getNom());
                SessionManager.setCurrentUserPrenom(loggedInUser.getPrenom());
                SessionManager.setCurrentUserEmail(loggedInUser.getEmail());
                SessionManager.setCurrentUserRole(loggedInUser.getRole());

                // Redirect based on user role
                if ("User".equals(loggedInUser.getRole())) {
                    loadLoggedScene("main.fxml");
                } else if ("Admin".equals(loggedInUser.getRole())) {
                    loadLoggedScene("tableau.fxml");
                } else {
                    lbl_status.setText("Invalid role for user.");
                }
            } else {
                lbl_status.setText("Invalid email or password.");
            }
        } catch (SQLException e) {
            lbl_status.setText("Database error: " + e.getMessage());
        }
        user currentUser = SessionManager.getCurrentUser();
        System.out.println("User ID: " + SessionManager.getCurrentUserId());
        System.out.println("User Name: " + SessionManager.getCurrentUserNom());
        System.out.println("User Prenom: " + SessionManager.getCurrentUserPrenom());

        System.out.println("User Email: " + SessionManager.getCurrentUserEmail());
        System.out.println("User Role: " + SessionManager.getCurrentUserRole());
// Log other user details as needed


    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void loadLoggedScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Logged In");
            stage.show();

            Stage currentStage = (Stage) tf_email.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            lbl_status.setText("Failed to load the logged-in scene.");
        }
    }

    @FXML
    private void goToSignUp() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sign-up.fxml")));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();

            Stage currentStage = (Stage) button_signup.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
