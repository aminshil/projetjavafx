package sample;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sample.SessionManager;

public class PasswordChangeController {

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private void handleChangePassword() {
        String currentHashedPassword = SessionManager.getCurrentUser().getPassword(); // Retrieve the current hashed password

        if (isPasswordCorrect(oldPasswordField.getText(), currentHashedPassword)) {
            if(updatePassword(newPasswordField.getText())) {
                showAlertDialog("Success", "Password successfully changed!", Alert.AlertType.INFORMATION);
            } else {
                showAlertDialog("Error", "Password change failed. Please try again.", Alert.AlertType.ERROR);
            }
        } else {
            showAlertDialog("Error", "Incorrect old password.", Alert.AlertType.ERROR);
        }
    }

    private boolean isPasswordCorrect(String password, String currentHashedPassword) {
        return hashPassword(password).equals(currentHashedPassword);
    }

    private boolean updatePassword(String newPassword) {
        String newHashedPassword = hashPassword(newPassword);
        // Assuming there is a method to update the password in the database for the current user
        return updateDatabaseWithNewPassword(newHashedPassword); // Implement this method to update the password in your database
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
            throw new RuntimeException(e);
        }
    }

    private void showAlertDialog(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Placeholder for actual database update method
    private boolean updateDatabaseWithNewPassword(String newHashedPassword) {
        try {
            // Here you should implement database update logic
            // For example:
            // return yourDatabase.updateUserPassword(SessionManager.getCurrentUserId(), newHashedPassword);
            return true; // Assuming update is always successful for demonstration
        } catch (Exception e) {
            return false; // Update failed
        }
    }
}
