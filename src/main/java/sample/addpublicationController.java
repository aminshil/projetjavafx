package sample;


import entite.Publication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.MouseEvent;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class addpublicationController {
    private final PublicationService sd = new PublicationService();
    private boolean update;
    private int id;

    Publication pub = null;
    ObservableList<Publication> pubList = FXCollections.observableArrayList();
    @FXML
    private TextField topicTextField;

    @FXML
    private TextField publicationTextField;
    @FXML
    private Label error;
    private String image;
    @FXML
    private Label imageStatusLabel;
    @FXML
    private ImageView uploadedImageView;

    private ListpublicationsutilisateurController listController;

    public void setListPublicationsutilisateurController(ListpublicationsutilisateurController controller) {
        this.listController = controller;
    }

    public addpublicationController() throws SQLException {
    }

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

                // Check if the uploaded file is an image
                if (!Files.probeContentType(targetPath).startsWith("image")) {
                    showAlert("Le fichier sélectionné n'est pas une image.");
                    return;
                }

                // If the file is an image, set the image path to just the filename
                this.image = selectedFile.getName(); // Extracting only the filename
                imageStatusLabel.setText(selectedFile.getName() + " uploaded");

                Image image = new Image(targetPath.toUri().toString());
                uploadedImageView.setImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    // Method to handle the action when the user clicks the "Ajouter Publication" button

    @FXML
    void ajouterpubbtn(MouseEvent event) throws SQLException {
        error.setText("");
        String[] forbiddenWords = {"cons", "idiot", "dump"};
        if (update == false) {
            if (topicTextField.getText().isEmpty() || publicationTextField.getText().isEmpty()) {
                showAlert("Veuillez remplir tous les champs du formulaire.");
                return;
            } else {
                for (String forbiddenWord : forbiddenWords) {
                    if (topicTextField.getText().contains(forbiddenWord) || publicationTextField.getText().contains(forbiddenWord)) {
                        error.setText("language offensive echec");
                        String censoredTopic = topicTextField.getText().replaceAll(forbiddenWord, "**");
                        String censoredPublication = publicationTextField.getText().replaceAll(forbiddenWord, "**");

                        error.setText("Language offensive, please avoid using: " + forbiddenWord);

                        topicTextField.setText(censoredTopic);
                        publicationTextField.setText(censoredPublication);
                    }
                }
            }
            if (topicTextField.getText().length() > 100) {
                showAlert("Le champ 'sujet' ne doit pas dépasser 100 caractères.");
                return;
            }

            if (publicationTextField.getText().length() > 100) {
                showAlert("Le champ 'Publication' ne doit pas dépasser 100 caractères.");
                return;
            }
            if (image == null || image.isEmpty()) {
                showAlert("Veuillez sélectionner une image");
                return;
            }

            // Here you should create a new Publication object and pass the image URL to it
            Publication newPublication = new Publication(publicationTextField.getText(), topicTextField.getText(), uploadedImageView.getImage().getUrl());

            // Now you can add this new Publication object to the database
            sd.add(newPublication);
            Stage stage = (Stage) topicTextField.getScene().getWindow();
            stage.close();
            listController.initialize();

        } else {
            // Update logic here
            // You can implement it similarly to how you handle the addition of a new publication
            try {
                Publication pub = sd.findById(id);
                pub.setTopicofpub(topicTextField.getText());
                pub.setPublication(publicationTextField.getText());
                pub.setImagepub(uploadedImageView.getImage().getUrl());

                sd.update(pub);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("Publication Modifier avec succès");
                alert.showAndWait();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                Logger.getLogger(afficherpubController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    void setUpdate ( boolean b){
        this.update = b;

    }

    private void showAlert (String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Publication getPub() {
        return new Publication(topicTextField.getText(),publicationTextField.getText(),uploadedImageView.getImage().getUrl());
    }

    public void initData (Publication pub){

        // Populate the form fields with the data of the selected Dons object
        topicTextField.setText(pub.getTopicofpub());

        //image.setText(don.getImage());
        publicationTextField.setText(pub.getPublication());
    }
}
