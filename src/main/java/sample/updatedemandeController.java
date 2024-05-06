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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.CategoryService;
import service.DemandeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class updatedemandeController {

    private final DemandeService rs = new DemandeService();
    @FXML
    private TextField sujet;

    @FXML
    private TextField demande;
    @FXML
    private ComboBox<String> urgence;
    @FXML
    private ComboBox<String> type;
    private final CategoryService categoryService = new CategoryService();


    private affichedemutController affichedemutController;

    public void setaffichedemutController(affichedemutController controller) {
        this.affichedemutController = controller;
    }

    public updatedemandeController() throws SQLException {
    }


    private void inventoryTypeList() {
        List<Category> categories = categoryService.getAll();
        ObservableList<String> typeList = FXCollections.observableArrayList();

        for (Category category : categories) {
            typeList.add(category.getTypeofcat());
        }

        type.setItems(typeList);
    }
    private String[] statusList = {"urgente", "normale", "faible"};

    public void inventoryStatusList() {

        List<String> statusL = new ArrayList<>();

        for (String data : statusList) {
            statusL.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(statusL);
        urgence.setItems(listData);

    }
    @FXML
    void submitupdate() throws SQLException {
        affichedemutController.selectedDemande.setNameofobj(sujet.getText());
        affichedemutController.selectedDemande.setDemande(demande.getText());
        affichedemutController.selectedDemande.setStateofdem(urgence.getSelectionModel().getSelectedItem().toString());
        // Get the selected category from ComboBox and update the Demande
        Category selectedCategory = categoryService.getCategoryByType(type.getSelectionModel().getSelectedItem());
        affichedemutController.selectedDemande.setCategory(selectedCategory);

        rs.update(affichedemutController.selectedDemande);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText("demande modifiée avec succés");
        alert.showAndWait();
        Stage stage = (Stage) demande.getScene().getWindow();
        stage.close();
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/listedem.fxml"));
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

    public void initData(Demande dem) {
        // Populate the form fields with the data of the selected Demande object
        sujet.setText(dem.getNameofobj());
        demande.setText(dem.getDemande());

        // Set the selected item for urgence ComboBox
        urgence.getSelectionModel().select(dem.getStateofdem());

        // Set the selected item for type ComboBox
        inventoryTypeList(); // Ensure the list is loaded
        if (dem.getCategory() != null) {
            type.getSelectionModel().select(dem.getCategory().getTypeofcat());
        }

    }

    public void initialize() {
        inventoryTypeList();
        inventoryStatusList();
    }
}


