package sample;

import entite.Demande;
import entite.user;
import jakarta.mail.MessagingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import service.DemandeService;
import service.userService;
import util.EmailUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class afficherdemController {

    public static Demande demande;
    @FXML
    private TableColumn<Demande, String> act;

    @FXML
    private TableView<Demande> Listdem;

    @FXML
    private TableColumn<Demande, String> DEM;

    @FXML
    private TableColumn<Demande, String> NAM;
    @FXML
    private TableColumn<Demande, String> STA;
    @FXML
    private Button addDemandeButton;
    @FXML
    private TextField search;

    private Stage primaryStage;

    public afficherdemController() throws SQLException {
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    @FXML
    void goToAddDemande() {
        try {



            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/addDemande.fxml"));
            Parent root = loader.load();
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



    private final DemandeService sd = new DemandeService();
    ObservableList<Demande> demList = FXCollections.observableArrayList();

    public void initialize() throws SQLException {
        loadData();
    }

    @FXML
    void loadData() throws SQLException {
        // Retrieve data from service layer
        List<Demande> demListFromService = sd.getAll();



        // Create a new ObservableList from the List
        demList = FXCollections.observableArrayList(demListFromService);

        // Set cell value factories
        NAM.setCellValueFactory(new PropertyValueFactory<>("nameofobj"));
        DEM.setCellValueFactory(new PropertyValueFactory<>("demande"));
        STA.setCellValueFactory(new PropertyValueFactory<>("stateofdem"));
        // Set the items in the TableView
        Listdem.setItems(demList);

        FilteredList<Demande> filteredList = new FilteredList<>(demList, b -> true);
        search.textProperty().addListener((observableValue, oldvalue, newvalue) -> {
            filteredList.setPredicate(demande -> {
                if (newvalue == null || newvalue.isEmpty() ) {
                    return true;
                }
                String key = newvalue.toLowerCase();
                if (demande.getNameofobj().toLowerCase().contains(key) || demande.getDemande().toLowerCase().contains(key)|| demande.getStateofdem().toLowerCase().contains(key) )
                {
                    return true;
                } else {
                    return false;
                }
            });
        });
        Listdem.refresh();
        SortedList<Demande> sorteddata = new SortedList<>(filteredList);
        sorteddata.comparatorProperty().bind(Listdem.comparatorProperty());
        Listdem.setItems(sorteddata);


        // Set cell factory for custom buttons
        Callback<TableColumn<Demande, String>, TableCell<Demande, String>> cellFactory =
                (TableColumn<Demande, String> param) -> new TableCell<Demande, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                            setText(null);
                            Listdem.refresh();
                        } else {
                            ImageView deleteIcon = new ImageView(getClass().getResource("publication/trash.png").toString());

                            deleteIcon.setFitWidth(25);
                            deleteIcon.setFitHeight(25);


                            deleteIcon.setOnMouseClicked(event -> {
                                demande = Listdem.getSelectionModel().getSelectedItem();
                                try {
                                    sd.delete(demande.getId());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                demList.remove(demande);
                                try {
                                    userService userService = new userService();
                                    user user = userService.getUserById(demande.getDemandeuser().getId());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                String userEmail = demande.getDemandeuser().getEmail();

                                    // Retrieve email configuration from environment variables
                                    String emailHost = "smtp.office365.com";
                                    String emailPort = "587";
                                    String emailUser = "EcopartageJava@outlook.fr";
                                    String emailPass = "Houssem10";
                                    String subject = "Demande Supprimé";
                                    String content = "l'admin a supprimé votre demande a cause de l'usage des mots inconviniantes.";
                                try {
                                    EmailUtil.sendEmail(emailHost, emailPort, emailUser, emailPass, userEmail, subject, content);
                                } catch (MessagingException e) {
                                    throw new RuntimeException(e);
                                }
                            });



                            HBox manageBtn = new HBox( deleteIcon);
                            manageBtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));


                            setGraphic(manageBtn);
                            setText(null);
                        }
                    }
                };

        act.setCellFactory(cellFactory);
    }
}

