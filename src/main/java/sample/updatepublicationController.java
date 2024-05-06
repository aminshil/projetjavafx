package sample;

import entite.Publication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.PublicationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class updatepublicationController {
    private final PublicationService rs = new PublicationService();
    @FXML
    private TextField topicTextField;
    @FXML
    private TextField publicationTextField;
    @FXML
    private ImageView uploadedImageView; // New ImageView for image display
    @FXML
    private Label imageStatusLabel; // Label to show image status

    public updatepublicationController() throws SQLException {
    }

    // New method to handle image upload
    @FXML
    private void handleUploadImageButtonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                Path targetDir = Paths.get("images");
                Files.createDirectories(targetDir);

                Path targetPath = targetDir.resolve(selectedFile.getName());
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                this.uploadedImageView.setImage(new Image(targetPath.toUri().toString()));
                imageStatusLabel.setText(selectedFile.getName() + " uploaded");
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to upload image: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    void submitupdate() {
        affichepubutController.selectedPublication.setTopicofpub(topicTextField.getText());
        affichepubutController.selectedPublication.setPublication(publicationTextField.getText());
        affichepubutController.selectedPublication.setImagepub(uploadedImageView.getImage().getUrl()); // Update image URL
        rs.update(affichepubutController.selectedPublication);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText("Publication modifiée avec succés");
        alert.showAndWait();
        Stage stage1 = (Stage) publicationTextField.getScene().getWindow();
        stage1.close();
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/liste.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            Scene scene = new Scene(root);
            newStage.setScene(scene);
            newStage.setTitle("liste des demandes");
            newStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initData(Publication pub) {
        topicTextField.setText(pub.getTopicofpub());
        publicationTextField.setText(pub.getPublication());
        uploadedImageView.setImage(new Image(pub.getImagepub())); // Set the current image
        imageStatusLabel.setText("Image courante"); // Set label text to indicate current image
    }
}
