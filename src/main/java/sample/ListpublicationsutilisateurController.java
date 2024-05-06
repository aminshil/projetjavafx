package sample;

import entite.Publication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.PublicationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListpublicationsutilisateurController {
    @FXML
    private VBox cardContainer; // VBox to contain the card components

    private PublicationService publicationService;

    // Method to initialize the controller
    @FXML
    public void initialize() throws SQLException {
        cardContainer.getChildren().clear(); // Clear the existing content
        publicationService = new PublicationService(); // Initialize the PublicationService
        loadPublications(); // Load publications and populate the card container
    }

    // Method to load publications and populate the card container
    private void loadPublications() throws SQLException {
        // Get all publications from the service
        List<Publication> publications = publicationService.getAllPublications();
        // Loop through each publication
        for (Publication publication : publications) {
            // Create labels for publication details
            Label publicationLabel = new Label("Publication: " + publication.getPublication());
            Label dateLabel = new Label("Date du Publication: " + publication.getDateofpub().toString());
            Label topicLabel = new Label("Le sujet de la Publication: " + publication.getTopicofpub());
            Label userLabel = new Label("L'utilisateur: " + (publication.getPublicationuser().getNom() + " " + publication.getPublicationuser().getPrenom()));

            // Create an ImageView for the publication image
            ImageView imageView = new ImageView();
            imageView.setFitWidth(100); // Set the width of the image
            imageView.setPreserveRatio(true); // Preserve the aspect ratio of the image

            // Load the image from the URL
            Image image = new Image(publication.getImagepub());
            imageView.setImage(image);

            // Create a hyperlink for "Show More"
            Label showMoreLabel = new Label("Voir plus");
            showMoreLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            // Add action for clicking on "Show More" (navigate to detailed view)
            showMoreLabel.setOnMouseClicked(e -> {
                try {
                    Stage stage1 = (Stage) cardContainer.getScene().getWindow();
                    stage1.close();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/affichePubUt.fxml"));
                    Parent root = loader.load();
                    // Pass the selected publication to the detailed view controller
                    affichepubutController controller = loader.getController();
                    controller.initData(publication);
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle(publication.getPublication());
                    stage.show();
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                }
            });
            // Create an HBox to contain the publication details
            HBox publicationHBox = new HBox(imageView, new VBox(publicationLabel, dateLabel, topicLabel, userLabel, showMoreLabel));
            publicationHBox.setStyle("-fx-spacing: 10px; -fx-padding: 10px; -fx-background-color: #f0f0f0;");
            HBox.setHgrow(publicationHBox, Priority.ALWAYS);

            // Add the HBox to the card container
            cardContainer.getChildren().add(publicationHBox);
        }
    }
    @FXML
    void goToAddPublication() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/addPublication.fxml"));
            Parent root = loader.load();
            addpublicationController addpubController = loader.getController();
            addpubController.setListPublicationsutilisateurController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error occurred while loading addPublication.fxml");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
