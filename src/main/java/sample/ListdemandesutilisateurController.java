package sample;

import entite.Category;
import entite.Demande;
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
import service.DemandeService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ListdemandesutilisateurController {
    @FXML
    private VBox cardContainer; // VBox to contain the card components

    private DemandeService demandeService;

    // Method to initialize the controller
    @FXML
    public void initialize() throws SQLException {
        cardContainer.getChildren().clear(); // Clear the existing content
        demandeService = new DemandeService(); // Initialize the DemandeService
        loadDemandes(); // Load demandes and populate the card container
    }



    // Method to load demandes and populate the card container
    private void loadDemandes() throws SQLException {
        // Get all demandes from the service
        List<Demande> demandes = demandeService.getAllDemandes();

        // Loop through each demande
        for (Demande demande : demandes) {
            // Create labels for demande details
            Label idLabel = new Label("Nom de l'objet: " + demande.getNameofobj());
            Label demandeLabel = new Label("La demande: " + demande.getDemande());
            Label dateLabel = new Label("Date du Demande: " + demande.getDateofdem().toString());
            Label categoryLabel = new Label("Category du Demande: " + (demande.getCategory() != null ? demande.getCategory().getTypeofcat() : "Unknown"));
            Label userLabel = new Label("L'utilsateur: " + demande.getDemandeuser().getNom() + " " + demande.getDemandeuser().getPrenom());
            ImageView categoryImageView = new ImageView();
            categoryImageView.setFitWidth(100);
            categoryImageView.setFitHeight(100);
            categoryImageView.setPreserveRatio(true);
            setImageForCategory(categoryImageView, demande.getCategory());


            // Create a hyperlink for "Show More"
            Label showMoreLabel = new Label("Voir plus");
            showMoreLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            // Add action for clicking on "Show More" (navigate to detailed view)
            showMoreLabel.setOnMouseClicked(e -> {
                try {
                    Stage stage1 = (Stage) cardContainer.getScene().getWindow();
                    stage1.close();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/afficheDemUt.fxml"));
                    Parent root = loader.load();
                    // Pass the selected demande to the detailed view controller
                    affichedemutController controller = loader.getController();
                    controller.initData(demande);
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle(demande.getDemande());
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // Create an HBox to contain the demande details
            HBox demandeHBox = new HBox(categoryImageView, new VBox(idLabel, demandeLabel, dateLabel, categoryLabel, userLabel, showMoreLabel));
            demandeHBox.setStyle("-fx-spacing: 10px; -fx-padding: 10px; -fx-background-color: #f0f0f0;");
            HBox.setHgrow(demandeHBox, Priority.ALWAYS);

            // Add the HBox to the card container
            cardContainer.getChildren().add(demandeHBox);
        }
    }

    private void setImageForCategory(ImageView imageView, Category category) {
        String imagePath = "publication/download.png"; // default image path
        if (category != null) {
            switch (category.getTypeofcat()) {
                case "nourriture":
                    imagePath = "publication/slide3.png";
                    break;
                case "Vêtement":
                    imagePath = "publication/slide2.png";
                    break;
                case "object":
                    imagePath = "publication/slide1.png";
                    break;
            }
        }
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream != null) {
            imageView.setImage(new Image(imageStream));
        } else {
            System.out.println("Image not found: " + imagePath);
        }
    }



    @FXML
    void goToAddDemande() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/addDemande.fxml"));
            Parent root = loader.load();
            adddemandeController addController = loader.getController();
            addController.setListDemandesutilisateurController(this);

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
