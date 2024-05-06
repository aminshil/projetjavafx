package sample;

import entite.formation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.formationService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static sample.SessionManager.getCurrentUserRole;

public class FormationController {

    @FXML
    private ListView<formation> formationListView;
    @FXML
    private Button ajButton;

    @FXML
    private ChoiceBox<String> sortChoiceBox;
    @FXML
    private TextField searchField;

    private ObservableList<formation> masterList = FXCollections.observableArrayList();


    private formationService formationService;

    public void initialize() {
        formationService = new formationService();
        loadFormations();
        setupCellFactory();
        setupSortChoiceBox();
        searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearch());
        String userRole = getCurrentUserRole(); // Implement this method based on your system
        if ("Admin".equals(userRole)) {
            ajButton.setVisible(true);
        } else {
            ajButton.setVisible(false);
        }

    }

    private void loadFormations() {
        try {
            List<formation> formations = formationService.getAllFormations();
            formationListView.getItems().clear(); // Efface la liste avant d'ajouter
            formationListView.getItems().addAll(formations);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goToAjoutFormation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/ajoutFormation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une formation");
            stage.show();
            Stage currentStage = (Stage) ajButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'exception
        }
    }

    private void setupCellFactory() {
        formationListView.setCellFactory(param -> new ListCell<formation>() {
            private final ImageView imageView = new ImageView();
            private final Label nameLabel = new Label();
            private final Label dateLabel = new Label();
            private final Label formateurLabel = new Label();
            private final Label lieuLabel = new Label();
            private final Button detailsButton = new Button("Détails");
            private final VBox contentVBox = new VBox(5);
            private final HBox hBox = new HBox(10);
            private final VBox vBox = new VBox(10);

            {
                contentVBox.getChildren().addAll(nameLabel, dateLabel, formateurLabel, lieuLabel, detailsButton);
                contentVBox.setPadding(new Insets(15));
                contentVBox.getStyleClass().add("card-content");

                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);

                hBox.getChildren().addAll(imageView, contentVBox);
                hBox.setAlignment(Pos.CENTER_LEFT);

                vBox.getChildren().add(hBox);
                vBox.getStyleClass().add("card");
            }

            @Override
            protected void updateItem(formation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        Image image;
                        if (item.getImageFormation() != null && !item.getImageFormation().isEmpty()) {
                            String imageUrl = item.getImageFormation();
                            URL url = new URL("file:" + imageUrl);

                            // Using smooth scaling to improve image quality
                            image = new Image(url.toExternalForm(), 150, 150, true, true);
                        } else {
                            // Provide a default image
                            image = new Image(getClass().getResourceAsStream("default_image.png"), 150, 150, true, true);
                        }
                        imageView.setImage(image);
                    } catch (Exception e) {
                        System.err.println("Error loading image: " + e.getMessage());
                        e.printStackTrace();
                        // Set a default image if the specified image fails to load
                        // imageView.setImage(new Image("C:/Users/Amin Shil/Downloads/empty.jpg", 150, 150, true, true));
                    }
                    nameLabel.setText(item.getNomFormation());
                    nameLabel.getStyleClass().add("card-title");
                    dateLabel.setText("Date: " + item.getDateFormation().toString());
                    formateurLabel.setText("Formateur: " + item.getFormateur());
                    lieuLabel.setText("Lieu: " + item.getLieuFormation());
                    detailsButton.setOnAction(e -> openDetailsView(item));
                    setGraphic(vBox);
                }
            }




            private void openDetailsView(formation item) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/formationDetails.fxml"));
                    Parent root = loader.load();
                    FormationDetailsController controller = loader.getController();
                    controller.initData(item);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Détails de la formation");
                    stage.show();
                    ((Stage) getScene().getWindow()).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void setupSortChoiceBox() {
        // Setup the sorting options
        sortChoiceBox.setItems(FXCollections.observableArrayList(
                "Nom", "Date", "Formateur", "Lieu"
        ));
        sortChoiceBox.setValue("Nom");  // Default value

        // Add a listener to handle changes
        sortChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    trierFormations(newVal);  // Call sort with new criterion
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void trierFormations(String criterion) throws SQLException {
        List<formation> formationList = formationService.getAllFormations();
        switch (criterion) {
            case "Nom":
                formationList.sort(Comparator.comparing(formation::getNomFormation));
                break;
            case "Date":
                formationList.sort(Comparator.comparing(formation::getDateFormation));
                break;
            case "Formateur":
                formationList.sort(Comparator.comparing(formation::getFormateur));
                break;
            case "Lieu":
                formationList.sort(Comparator.comparing(formation::getLieuFormation));
                break;
        }

        // Update the list of formations in the view
        formationListView.getItems().setAll(formationList);
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (!searchTerm.isEmpty()) {
            try {
                List<formation> filteredFormations = formationService.getAllFormations().stream()
                        .filter(f -> f.getNomFormation().toLowerCase().contains(searchTerm.toLowerCase()))
                        .collect(Collectors.toList());
                formationListView.getItems().setAll(filteredFormations); // Utilise setAll pour éviter les doublons
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            loadFormations(); // Recharge toutes les formations si la recherche est vide
        }
    }


}
