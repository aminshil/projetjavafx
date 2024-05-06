package sample;

import entite.Publication;
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
import service.PublicationService;
import util.EmailUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class afficherpubController {

    public static Publication publication;
    @FXML
    private TableColumn<Publication, String> act;

    @FXML
    private TableView<Publication> Listpub;

    @FXML
    private TableColumn<Publication, String> PUB;

    @FXML
    private TableColumn<Publication, String> SUJ;
    @FXML
    private Button addPublicationButton;

    @FXML
    private TextField search;

    public afficherpubController() throws SQLException {
    }


    @FXML
    void goToAddPublication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/addPublication.fxml"));
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


    private final PublicationService sd = new PublicationService();
    ObservableList<Publication> pubList = FXCollections.observableArrayList();

    public void initialize() {
        loadData();
    }

    @FXML
    void loadData() {
        // Retrieve data from service layer
        List<Publication> pubsListFromService = sd.getAll();

        // Create a new ObservableList from the List
        pubList = FXCollections.observableArrayList(pubsListFromService);

        // Set cell value factories
        SUJ.setCellValueFactory(new PropertyValueFactory<>("topicofpub"));
        PUB.setCellValueFactory(new PropertyValueFactory<>("publication"));

        // Set the items in the TableView
        Listpub.setItems(pubList);

        FilteredList<Publication> filteredList = new FilteredList<>(pubList, b -> true);
        search.textProperty().addListener((observableValue, oldvalue, newvalue) -> {
            filteredList.setPredicate(publication -> {
                if (newvalue == null || newvalue.isEmpty() ) {
                    return true;
                }
                String key = newvalue.toLowerCase();
                if (publication.getTopicofpub().toLowerCase().contains(key) || publication.getPublication().toLowerCase().contains(key) )
                {
                    return true;
                } else {
                    return false;
                }
            });
        });
        Listpub.refresh();
        SortedList<Publication> sorteddata = new SortedList<>(filteredList);
        sorteddata.comparatorProperty().bind(Listpub.comparatorProperty());
        Listpub.setItems(sorteddata);


        // Set cell factory for custom buttons
        Callback<TableColumn<Publication, String>, TableCell<Publication, String>> cellFactory =
                (TableColumn<Publication, String> param) -> new TableCell<Publication, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                            Listpub.refresh();
                        } else {
                            ImageView deleteIcon = new ImageView(getClass().getResource("publication/trash.png").toString());

                            deleteIcon.setFitWidth(25);
                            deleteIcon.setFitHeight(25);


                            deleteIcon.setOnMouseClicked(event -> {
                                publication = Listpub.getSelectionModel().getSelectedItem();
                                try {
                                    sd.delete(publication.getId());
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                pubList.remove(publication);


                                String userEmail = publication.getPublicationuser().getEmail();
                                System.out.println(userEmail);
                                // Retrieve email configuration from environment variables
                                String emailHost = "smtp.office365.com";
                                String emailPort = "587";
                                String emailUser = "EcopartageJava@outlook.fr";
                                String emailPass = "Houssem10";
                                String subject = "publication Supprimé";
                                String content = "l'admin a supprimé votre publication a cause de l'usage des mots inconviniantes.";
                                try {
                                    EmailUtil.sendEmail(emailHost, emailPort, emailUser, emailPass, userEmail, subject, content);
                                } catch (MessagingException e) {
                                    throw new RuntimeException(e);
                                }
                            });


                            HBox manageBtn = new HBox( deleteIcon);
                            manageBtn.setStyle("-fx-alignment:center");
                            HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));
                            ;

                            setGraphic(manageBtn);
                            setText(null);
                        }
                    }
                };

        act.setCellFactory(cellFactory);
    }
}
