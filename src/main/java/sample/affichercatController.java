package sample;

import entite.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import service.CategoryService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class affichercatController {

    public static Category category;
    @FXML
    private TableColumn<Category, String> act;

    @FXML
    private TableView<Category> Listcat;

    @FXML
    private TableColumn<Category, String> TYP;


    @FXML
    private Button addCategoryButton;

    public affichercatController() throws SQLException {
    }


    @FXML
    void goToAddCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/addCategory.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error occurred while loading addCategory.fxml");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }


    private final CategoryService sd = new CategoryService();
    ObservableList<Category> catList = FXCollections.observableArrayList();

    public void initialize() {
        loadData();
    }

    @FXML
    void loadData() {
        // Retrieve data from service layer
        List<Category> donsListFromService = sd.getAll();

        // Create a new ObservableList from the List
        catList = FXCollections.observableArrayList(donsListFromService);

        // Set cell value factories
        TYP.setCellValueFactory(new PropertyValueFactory<>("typeofcat"));


        // Set the items in the TableView
        Listcat.setItems(catList);

        // Set cell factory for custom buttons
        Callback<TableColumn<Category, String>, TableCell<Category, String>> cellFactory =
                (TableColumn<Category, String> param) -> new TableCell<Category, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                            setText(null);
                            Listcat.refresh();
                        } else {
                            ImageView deleteIcon = new ImageView(getClass().getResource("/trash.png").toString());
                            ImageView editIcon = new ImageView(getClass().getResource("/message.png").toString());
                            deleteIcon.setFitWidth(25);
                            deleteIcon.setFitHeight(25);
                            editIcon.setFitWidth(25);
                            editIcon.setFitHeight(25);

                            deleteIcon.setOnMouseClicked(event -> {
                                category = Listcat.getSelectionModel().getSelectedItem();
                                sd.delete(category.getId());
                                catList.remove(category);
                            });

                            editIcon.setOnMouseClicked(event -> {
                                category = Listcat.getSelectionModel().getSelectedItem();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/updatecategory.fxml"));
                                Parent root;
                                try {
                                    root = loader.load();
                                    updatecategoryController controller = loader.getController();
                                    controller.initData(category); // Pass the selected Dons object
                                    Scene scene = new Scene(root);
                                    Stage primaryStage = new Stage();
                                    primaryStage.setScene(scene);
                                    primaryStage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                            HBox manageBtn = new HBox(editIcon, deleteIcon);
                            manageBtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));
                            HBox.setMargin(editIcon, new Insets(2, 3, 0, 2));

                            setGraphic(manageBtn);
                            setText(null);
                        }
                    }
                };

        act.setCellFactory(cellFactory);
    }
}

