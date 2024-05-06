package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entite.user;
import service.userService;

import java.io.File;
import java.sql.SQLException;

public class UpdateUserController {
    @FXML private TextField tfName;
    @FXML private TextField tfEmail;
    @FXML private TextField tfPrenom;
    @FXML private PasswordField tfPassword;
    @FXML private ComboBox<String> cbRole;
    @FXML private DatePicker dpBirthday;
    @FXML private ImageView imageView;

    private userService userService;
    private user currentUser;
    private File selectedImageFile;

    public void initData(user user) throws SQLException {
        currentUser = user;
        tfName.setText(user.getNom());
        tfEmail.setText(user.getEmail());
        tfPrenom.setText(user.getPrenom());
        tfPassword.setText(user.getPassword());
        cbRole.getItems().setAll("Admin", "User", "Societe");
        cbRole.setValue(user.getRole());
        dpBirthday.setValue(user.getDateNaissance());
        if (user.getImagePath() != null && !user.getImagePath().isEmpty()) {
            File file = new File(user.getImagePath());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
        userService = new userService();
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        selectedImageFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedImageFile != null) {
            imageView.setImage(new Image(selectedImageFile.toURI().toString()));
            currentUser.setImagePath(selectedImageFile.getAbsolutePath());
        }
    }

    @FXML
    private void saveUser() {
        try {
            currentUser.setNom(tfName.getText());
            currentUser.setEmail(tfEmail.getText());
            currentUser.setPrenom(tfPrenom.getText());
            currentUser.setPassword(tfPassword.getText());
            currentUser.setRole(cbRole.getValue());
            currentUser.setDateNaissance(dpBirthday.getValue());

            userService.updateUser(currentUser);

            Stage stage = (Stage) tfName.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Error Updating User");
            alert.setContentText("Unable to update user: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
