package sample;


import entite.Category;
import entite.Demande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.CategoryService;
import service.DemandeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class adddemandeController {

    private final DemandeService sd = new DemandeService();
    private boolean update;
    private int id;
    private final DemandeService demandeService = new DemandeService();
    private final CategoryService categoryService = new CategoryService();

    Demande dem = null;
    ObservableList<Demande> demList = FXCollections.observableArrayList();

    @FXML
    private TextField sujet;

    @FXML
    private TextField demande;

    @FXML
    private ComboBox<String> type;

    @FXML
    private ComboBox<String> urgence;
    @FXML
    private Label error;

    private ListdemandesutilisateurController listController;

    public void setListDemandesutilisateurController(ListdemandesutilisateurController controller) {
        this.listController = controller;
    }

    public adddemandeController() throws SQLException {
    }

    // Method to handle the action when the user clicks the "Ajouter Demande" button
    @FXML
    void ajouterdembtn(MouseEvent event) {
        error.setText("");

        String[] forbiddenWords = {"cons", "idiot", "dump"};
        if (update == false) {
            if (sujet.getText().isEmpty() || demande.getText().isEmpty()|| urgence.getValue().isEmpty()|| type.getValue().isEmpty()) {
                showAlert("Veuillez remplir tous les champs du formulaire.");
                return;
            } else {
                for (String forbiddenWord : forbiddenWords) {
                    if (sujet.getText().contains(forbiddenWord) || demande.getText().contains(forbiddenWord)) {
                        error.setText("language offensive echec");
                        String censoredTopic = sujet.getText().replaceAll(forbiddenWord, "**");
                        String censoredPublication = demande.getText().replaceAll(forbiddenWord, "**");

                        error.setText("Language offensive, please avoid using: " + forbiddenWord);

                        sujet.setText(censoredTopic);
                        demande.setText(censoredPublication);
                    }
                }
            }
            if (sujet.getText().length() > 100) {
                showAlert("Le champ 'object' ne doit pas dépasser 100 caractères.");
                return;
            }

            if (demande.getText().length() > 100) {
                showAlert("Le champ 'Demande' ne doit pas dépasser 100 caractères.");
                return;
            }

            try {
                // Get the selected type of category
                String selectedType = type.getSelectionModel().getSelectedItem();
                // Fetch the category based on the selected type
                Category category = categoryService.getCategoryByType(selectedType);
                // Add the demande
                sd.add(new Demande(demande.getText() ,sujet.getText(), urgence.getSelectionModel().getSelectedItem(), category));
                System.out.println(sujet.getText());
                System.out.println(demande.getText());
                Stage stage = (Stage) sujet.getScene().getWindow();
                stage.close();
                listController.initialize();
            } catch (SQLException e) {
                handleSQLException(e);
                return;
            }
        } else {
            try {
                Demande dem = sd.findById(id);
                dem.setDemande(demande.getText());
                dem.setNameofobj(sujet.getText());
                dem.setStateofdem(urgence.getSelectionModel().getSelectedItem());
                // Get the selected type of category
                String selectedType = type.getSelectionModel().getSelectedItem();
                // Fetch the category based on the selected type
                Category category = categoryService.getCategoryByType(selectedType);
                dem.setCategory(category);
                sd.update(dem);
                showAlert("Demande modifiée avec succès");
                Stage stage = (Stage) sujet.getScene().getWindow();
                stage.close();
                listController.initialize();
            } catch (SQLException e) {
                handleSQLException(e);
                return;
            }
        }

    }



    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initialize() {
        inventoryTypeList();
        inventoryStatusList();
    }

    private void inventoryTypeList() {
        List<Category> categories = categoryService.getAll();
        ObservableList<String> typeList = FXCollections.observableArrayList();

        for (Category category : categories) {
            typeList.add(category.getTypeofcat());
        }

        type.setItems(typeList);
    }

    private void inventoryStatusList() {
        ObservableList<String> listData = FXCollections.observableArrayList("urgente", "normale", "faible");
        urgence.setItems(listData);
    }

    void setUpdate(boolean b) {
        this.update = b;
    }

    private void handleSQLException(SQLException e) {
        showAlert("Erreur SQL: " + e.getMessage());
        Logger.getLogger(afficherdemController.class.getName()).log(Level.SEVERE, null, e);
    }
}