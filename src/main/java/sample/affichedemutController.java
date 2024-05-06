package sample;

import entite.Comment;
import entite.Demande;
import entite.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.CommentService;
import service.DemandeService;
import service.userService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static sample.SessionManager.*;

public class affichedemutController {
    @FXML
    private Label demandeLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label stateLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label categoryLabel;
    @FXML
    private Label idLabel;

    @FXML
    private Label userLabel;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private VBox commentsContainer;

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    public static Demande demande;
    static Demande selectedDemande;
    private final CommentService commentService = new CommentService();

    private final DemandeService sd = new DemandeService();

    public affichedemutController() throws SQLException {
    }

    // Method to initialize the controller
    public void initData(Demande demande) {
        selectedDemande = demande; // Set the selected demande
        // Set demande details
        idLabel.setText(demande.getDemandeuser().getNom() + " " + demande.getDemandeuser().getPrenom());
        demandeLabel.setText(demande.getDemande());
        nameLabel.setText(demande.getNameofobj());
        stateLabel.setText(demande.getStateofdem());
        dateLabel.setText(demande.getDateofdem().toString());
        categoryLabel.setText(demande.getCategory().getTypeofcat());

        loadComments();
        adjustButtonVisibility();
    }

    private void adjustButtonVisibility() {
        // Assume a method getCurrentUserId() returns the ID of the current user
        boolean isOwner = selectedDemande.getDemandeuser().getId() == getCurrentUserId();
        boolean isAdmin = Objects.equals(getCurrentUserRole(), "Admin");

        btnEdit.setVisible(isOwner);
        btnDelete.setVisible(isOwner || isAdmin);
    }

    @FXML
    private void deleteDemande() throws SQLException {
        // Check if selectedDemande is not null
        if (selectedDemande != null) {
            // Call the delete method in the service
            sd.delete(selectedDemande.getId());
            Stage stage = (Stage) idLabel.getScene().getWindow(); // Get the current stage
            stage.close();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/listedem.fxml"));
                Parent root = loader.load();
                Stage newStage = new Stage();
                Scene scene = new Scene(root);
                newStage.setScene(scene);
                newStage.setTitle("Liste des demandes");
                newStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Update the UI to reflect the changes (remove the deleted demande from the view)
            // You may need to reload the demandes list or update the view accordingly
        } else {
            // Handle the case where no demande is selected
            // You can display an error message or log a warning
            showAlert("No demande selected.");
        }
    }
/*
    @FXML
    private void editDemande() {
        // Check if selectedDemande is not null
        if (selectedDemande != null) {
            try {
                // Load the FXML file for the update demande view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/updatedemande.fxml"));
                Parent root = loader.load();
                // Get the controller for the update demande view
                updatedemandeController controller = loader.getController();
                // Initialize the data for the update demande view using initData() method
                controller.initData(selectedDemande);
                // Show the update demande view
                Scene scene = new Scene(root);
                Stage primaryStage = new Stage();
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where no demande is selected
            // You can display an error message or log a warning
            showAlert( "No demande selected.");
        }
    }*/


    @FXML
    private void editedem() {
        // Check if selectedPublication is not null
        Stage stage = (Stage) stateLabel.getScene().getWindow();
        stage.close();
        if (selectedDemande != null) {
            // Open a new window or dialog for editing the publication
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/updatedemande.fxml"));
                Parent root = loader.load();

                // Get the controller for the edit publication window
                updatedemandeController editController = loader.getController();
                editController.setaffichedemutController(this);
                // Pass the selected publication to the editController
                editController.initData(selectedDemande);

                // Display the edit publication window
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Error occurred while loading editDemadne.fxml");
            }
        } else {
            showAlert("No demande selected.");
        }
    }
    private void loadComments() {
        List<Comment> comments = commentService.getCommentsForEntity(selectedDemande.getId(), false); // false indicates it's for Demande
        commentsContainer.getChildren().clear();
        for (Comment comment : comments) {
            buildCommentBox(comment);
        }
    }
    @FXML
    private void submitComment() throws SQLException {
        if (selectedDemande != null && !commentTextArea.getText().isEmpty()) {
            user newUser = getCurrentUser();
            Comment newComment = new Comment(commentTextArea.getText(), selectedDemande, newUser);
            commentService.addComment(newComment);
            commentTextArea.clear();
            loadComments();
        } else {
            showAlert("Please enter a comment or select a demande.");
        }
    }

    private void buildCommentBox(Comment comment) {
        Label userNameLabel = new Label(comment.getUser().getNom()+" "+comment.getUser().getPrenom()+":"); // This should dynamically set based on the user
        userNameLabel.setStyle("-fx-font-weight: bold;");
        Label commentContentLabel = new Label(comment.getComment());
        TextField editTextField = new TextField(comment.getComment());
        editTextField.setVisible(false);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = comment.getDateofcom().format(formatter);
        Label commentDateLabel = new Label(formattedDate);
        commentDateLabel.setStyle("-fx-font-size: 10px;");

        // Create update and delete buttons
        Button updateButton = new Button();
        Button deleteButton = new Button();

        // Set preferred size for the buttons
        updateButton.setPrefSize(15, 15);
        deleteButton.setPrefSize(15, 15);

        // Create ImageViews for update and delete button icons
        ImageView updateIcon = new ImageView(new Image(getClass().getResourceAsStream("publication/message.png")));
        ImageView deleteIcon = new ImageView(new Image(getClass().getResourceAsStream("publication/trash.png")));

        // Set fit width and height for the icons
        updateIcon.setFitWidth(15);
        updateIcon.setFitHeight(15);
        deleteIcon.setFitWidth(15);
        deleteIcon.setFitHeight(15);

        // Set icons for update and delete buttons
        updateButton.setGraphic(updateIcon);
        deleteButton.setGraphic(deleteIcon);

        updateButton.setOnAction(event -> updateComment(comment, editTextField, commentContentLabel));
        deleteButton.setOnAction(event -> deleteComment(comment, commentsContainer));

        HBox commentBox = new HBox(userNameLabel, commentContentLabel, commentDateLabel, editTextField, updateButton, deleteButton);
        updateButton.setVisible(false);
        deleteButton.setVisible(false);

        if (comment.getUser().getId() == getCurrentUserId()) {
            // Allow editing and deleting for comment owner
            updateButton.setVisible(true);
            deleteButton.setVisible(true);
        }

        if (Objects.equals(getCurrentUserRole(), "Admin")) {
            // Admins can only delete comments
            deleteButton.setVisible(true);
        }

        commentBox.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
        commentsContainer.getChildren().add(commentBox);
    }

    private void updateComment(Comment comment, TextField editTextField, Label commentContentLabel) {
        if (!editTextField.isVisible()) {
            editTextField.setVisible(true);
            commentContentLabel.setVisible(false);
            editTextField.requestFocus();
        } else {
            commentService.updateComment(comment.getId(), editTextField.getText());
            commentContentLabel.setText(editTextField.getText());
            editTextField.setVisible(false);
            commentContentLabel.setVisible(true);
        }
    }

    private void deleteComment(Comment comment, VBox container) {
        commentService.deleteComment(comment.getId());
        container.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                HBox box = (HBox) node;
                Label label = (Label) box.getChildren().get(1); // Assuming comment content is the second child
                return label.getText().equals(comment.getComment());
            }
            return false;
        });
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

}

