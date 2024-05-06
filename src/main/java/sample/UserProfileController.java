package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import entite.user;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class UserProfileController {
    @FXML
    private Label labelName;
    @FXML
    private Label labelEmail;
    @FXML
    private Label labelId;

    @FXML
    private Label labelDateNaissance;

    @FXML
    private ImageView imageViewPhoto;
    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        user currentUser = SessionManager.getCurrentUser();
        labelName.setText(currentUser.getNom() + " " + currentUser.getPrenom());
        labelEmail.setText(currentUser.getEmail());
        labelId.setText(String.valueOf(currentUser.getId()));

        updateProfile();

    }

    private void updateProfile() {
        if (SessionManager.getCurrentUser() != null) {
            // Update date of birth
            if (SessionManager.getCurrentUser().getDateNaissance() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
                String formattedDate = SessionManager.getCurrentUser().getDateNaissance().format(formatter);
                labelDateNaissance.setText(formattedDate);
            } else {
                labelDateNaissance.setText("Date of Birth: Not Available");
            }

            String imagePath = SessionManager.getCurrentUser().getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Image image = new Image("file:" + imagePath);
                    imageViewPhoto.setImage(image);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error loading image: " + e.getMessage());
                    // Optionally set a default image if the provided one is invalid
                    imageViewPhoto.setImage(new Image("file:/path/to/default.png"));
                }
            } else {
                // Optionally set a default image if none is provided
                imageViewPhoto.setImage(new Image("file:/path/to/default.png"));
            }
        }
    }

    @FXML
    private void openPasswordChangeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/PasswordChangeDialog.fxml"));

            // Ensure location is set
            if (loader.getLocation() == null) {
                throw new IllegalStateException("FXML file location is not set.");
            }

            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Change Password");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        // Log out logic here
        System.out.println("User logged out.");

        // Close the current stage or window
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();

        // Optionally, open the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/hello.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}






