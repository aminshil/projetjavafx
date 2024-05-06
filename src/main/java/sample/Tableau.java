package sample;

import jakarta.mail.MessagingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JFrame;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import entite.user;
import service.userService;
import java.util.List;  // Make sure this is imported
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javafx.scene.control.ListCell;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
public class Tableau {

    @FXML
    private TableView<user> tableView;
    @FXML
    private Button logoutButton;
    @FXML
    private TableColumn<user, Integer> id_col;
    @FXML
    private ComboBox<String> sortComboBox;


    @FXML
    private TableColumn<user, String> nom_col;

    @FXML
    private TableColumn<user, String> prenom_col;

    @FXML
    private TableColumn<user, String> email_col;

    @FXML
    private TableColumn<user, String> password_col;

    @FXML
    private ListView<user> listView;
    @FXML
    private Label labelCurrentUserInfo;
    @FXML
    private Label labelCurrentUser;
    @FXML
    private TextField searchField;

    private userService userService;
    private ObservableList<user> allUsers = FXCollections.observableArrayList();
    private ObservableList<user> filteredUsers = FXCollections.observableArrayList();

    @FXML
    private void initialize() throws SQLException {
        userService = new userService();
        configureListView();
        chargerDonnees();
        setupSortComboBox();

        // Set the current user label text
        user currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            labelCurrentUser.setText("Connected as: " + currentUser.getNom() + " " + currentUser.getPrenom());
            labelCurrentUserInfo.setText( currentUser.getEmail());
        }

        searchField.textProperty().addListener(obs -> updateFilteredData());
    }

    private void chargerDonnees() {
        try {
            List<user> userList = userService.getAllUsers();
            allUsers.setAll(userList);
            sortAndFilterUsers(sortComboBox.getSelectionModel().getSelectedItem());
        } catch (SQLException e) {
            e.printStackTrace(); // Proper error handling should be implemented
        }
    }


    private void updateFilteredData() {
        String filterString = searchField.getText();
        if (filterString == null || filterString.isEmpty()) {
            filteredUsers.setAll(allUsers);
        } else {
            filteredUsers.setAll(allUsers.stream()
                    .filter(user -> user.getNom().toLowerCase().contains(filterString.toLowerCase()) ||
                            user.getPrenom().toLowerCase().contains(filterString.toLowerCase()) ||
                            user.getEmail().toLowerCase().contains(filterString.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        listView.setItems(filteredUsers);
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        user selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Delete user");
        confirmation.setContentText("Are you sure you want to delete this user?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.deleteUser(selectedUser);
                    allUsers.remove(selectedUser);
                    updateFilteredData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleExitButton(ActionEvent event) {
        System.out.println("Exit button clicked!");
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }
    @FXML
    private void handleUpdateButton(ActionEvent event) throws IOException, SQLException {
        user selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Missing");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user to update.");
            alert.showAndWait();
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/UserUpdateForm.fxml"));
            Parent root = loader.load();

            UpdateUserController controller = loader.getController();
            controller.initData(selectedUser);

            Stage stage = new Stage();
            stage.setTitle("Update User");
            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    private void configureListView() {
        listView.setCellFactory(lv -> new ListCell<user>() {
            private ImageView imageView = new ImageView();

            @Override
            protected void updateItem(user item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null); // Reset style
                } else {
                    if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                        File file = new File(item.getImagePath());
                        if (file.exists()) {
                            Image image = new Image(file.toURI().toString(), 200, 200, true, true);
                            imageView.setImage(image);
                        } else {
                            imageView.setImage(new Image("path/to/default/image.png", 200, 200, true, true));
                        }
                    }
                    String userInfo = String.format("ID: %d\nName: %s %s\nEmail: %s\nRole: %s\nDate de Naissance: %s\nBlocked: %s",
                            item.getId(), item.getNom(), item.getPrenom(),
                            item.getEmail(), item.getRole(),
                            item.getDateNaissance() != null ? item.getDateNaissance().toString() : "N/A",
                            item.isBlocked() ? "Yes" : "No");

                    setText(userInfo);
                    setGraphic(imageView);
                    // Apply CSS styles defined in styles.css
                    getStyleClass().add("list-cell");

                    if (isSelected()) {
                        getStyleClass().add("selected");
                    } else {
                        getStyleClass().remove("selected");
                    }
                }
            }
        });
    }




    @FXML
    private void handleBlockUser(ActionEvent event) {
        user selectedUser = listView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                userService.toggleUserBlock(selectedUser.getId(), !selectedUser.isBlocked());
                selectedUser.setBlocked(!selectedUser.isBlocked()); // Mettre à jour l'état local
                // Mettre à jour l'affichage ou rafraîchir les données
            } catch (SQLException e) {
                e.printStackTrace(); // Gérer les erreurs de base de données
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void handleVerifyButton(ActionEvent event) {
        try {
            int verifiedUsers = userService.countVerifiedUsers(true);
            int nonVerifiedUsers = userService.countVerifiedUsers(false);

            // Assuming you are using JFreeChart for graph generation
            showVerificationChart(verifiedUsers, nonVerifiedUsers);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception properly
        }
    }

    private void showVerificationChart(int verifiedUsers, int nonVerifiedUsers) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(verifiedUsers, "Users", "Verified");
        dataset.setValue(nonVerifiedUsers, "Users", "Non-Verified");

        JFreeChart chart = ChartFactory.createBarChart(
                "User Verification", // chart title
                "Category", // domain axis label
                "Number", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true,
                false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        JFrame frame = new JFrame("Verification Statistics");
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    @FXML
    private void handleDownloadPdfButton(ActionEvent event) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            // Set the offset for the text
            contentStream.newLineAtOffset(25, 725);

            List<user> users = userService.getAllUsers();
            for (user usr : users) {
                String userInfo = "ID: " + usr.getId() + ", Name: " + usr.getNom() + " " + usr.getPrenom() + ", Email: " + usr.getEmail();
                contentStream.showText(userInfo);
                contentStream.newLineAtOffset(0, -15); // Move to the next line
            }

            contentStream.endText();
            contentStream.close();

            // Save the document to a file
            document.save("Liste des Utilisateurs.pdf");
            document.close();

            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION, "PDF generated successfully and saved as 'UserList.pdf'.", ButtonType.OK);
            infoAlert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error generating PDF.", ButtonType.OK);
            errorAlert.showAndWait();
        }
    }

    private void setupSortComboBox() {
        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            sortAndFilterUsers(newVal);
        });
        sortComboBox.getSelectionModel().selectFirst(); // Default to 'All'
    }

    private void sortAndFilterUsers(String role) {
        if (role == null || role.equals("All")) {
            filteredUsers.setAll(allUsers);
        } else {
            filteredUsers.setAll(allUsers.stream()
                    .filter(user -> user.getRole().equalsIgnoreCase(role))
                    .collect(Collectors.toList()));
        }
        listView.setItems(filteredUsers);
    }
    @FXML
    private void handleLogoutButton(ActionEvent event) {
        // Log out logic here
        System.out.println("User logged out.");

        // Close the current stage or window
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();

        // Optionally, open the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/hello.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleShowFormations(ActionEvent event) {
        try {
            // Load the FXML file for Formations
            Parent formationsView = FXMLLoader.load(getClass().getResource("formation/afficherformation.fxml"));

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the current stage
            stage.setScene(new Scene(formationsView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors possibly with a dialog or logger
        }
    }

    public void handleShowListdemadmin(ActionEvent event) {
        try {
            // Load the FXML file for Formations
            Parent formationsView = FXMLLoader.load(getClass().getResource("publication/afficheDemande.fxml"));

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the current stage
            stage.setScene(new Scene(formationsView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors possibly with a dialog or logger
        }
    }

    public void handleShowListpubadmin(ActionEvent event) {
        try {
            // Load the FXML file for Formations
            Parent formationsView = FXMLLoader.load(getClass().getResource("publication/affichePublication.fxml"));

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the current stage
            stage.setScene(new Scene(formationsView));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors possibly with a dialog or logger
        }
    }

}



