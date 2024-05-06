package sample;

import entite.Comment;
import entite.Publication;
import entite.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.CommentService;
import service.PublicationService;
import service.userService;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static sample.SessionManager.*;

public class affichepubutController {
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    public ScrollPane commentsScrollPane;
    @FXML
    private Label publicationLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label topicLabel;
    @FXML
    private ImageView DonImage;  // Ensure this ID matches with FXML
    @FXML
    private Label userLabel;
    @FXML
    private TextArea commentTextArea;

    static Publication selectedPublication;
    @FXML
    private VBox commentsContainer;
    private final PublicationService sd = new PublicationService();
    private final CommentService commentService = new CommentService();
private final userService us = new userService();
    public affichepubutController() throws SQLException {
    }

    public void initData(Publication publication) throws SQLException {
        selectedPublication = publication;
        publicationLabel.setText(publication.getPublication());
        dateLabel.setText(publication.getDateofpub().toString());
        topicLabel.setText(publication.getTopicofpub());
        userLabel.setText(publication.getPublicationuser().getNom() + " " + publication.getPublicationuser().getPrenom());

        loadImage();
        loadComments();
        adjustButtonVisibility();
    }
    private void adjustButtonVisibility() {
        // Assume a method getCurrentUserId() returns the ID of the current user
        boolean isOwner = selectedPublication.getPublicationuser().getId() == getCurrentUserId();
        boolean isAdmin = Objects.equals(getCurrentUserRole(), "Admin");

        btnEdit.setVisible(isOwner);
        btnDelete.setVisible(isOwner || isAdmin);
    }
    private void loadImage() {
        try {
            // Here we create a new Image directly from the URI string
            Image image = new Image(selectedPublication.getImagepub());
            DonImage.setImage(image);
            DonImage.setFitWidth(100);
            DonImage.setPreserveRatio(true);
        } catch (Exception e) {
            e.printStackTrace();  // This will help in debugging
            showAlert("Error loading image: " + e.getMessage());
        }
        System.out.println("Image path: " + selectedPublication.getImagepub());

    }



    @FXML
    private void deletePublication() throws SQLException {
        if (selectedPublication != null) {
            sd.delete(selectedPublication.getId());
            Stage stage1 = (Stage) publicationLabel.getScene().getWindow();
            stage1.close();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/liste.fxml"));
                Parent root = loader.load();
                // Display the edit publication window
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Error occurred while loading liste.fxml");
            }


            showAlert("Publication supprimée avec succés.");
        } else {
            showAlert("No publication selected.");
        }
    }

    @FXML
    private void editepub() {
        // Check if selectedPublication is not null
        if (selectedPublication != null) {
            // Open a new window or dialog for editing the publication
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/updatepublication.fxml"));
                Parent root = loader.load();

                // Get the controller for the edit publication window
                updatepublicationController editController = loader.getController();

                // Pass the selected publication to the editController
                editController.initData(selectedPublication);

                // Display the edit publication window
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert("Error occurred while loading editPublication.fxml");
            }
        } else {
            showAlert("No publication selected.");
        }

    }
    private void loadComments() {
        List<Comment> comments = commentService.getCommentsForEntity(selectedPublication.getId(), true);
        commentsContainer.getChildren().clear();
        for (Comment comment : comments) {
            Label userNameLabel = new Label(comment.getUser().getNom()+ " " + comment.getUser().getPrenom());
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

            HBox commentBox = new HBox(5, userNameLabel, commentContentLabel, editTextField, commentDateLabel, updateButton, deleteButton);
            commentBox.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-padding: 5px;");
            HBox.setHgrow(commentDateLabel, Priority.ALWAYS);
            commentBox.setAlignment(Pos.CENTER_LEFT); // Align contents to the left center

            updateButton.setOnAction(event -> {
                // Toggle visibility of text field and label for editing
                commentContentLabel.setVisible(!commentContentLabel.isVisible());
                editTextField.setVisible(!editTextField.isVisible());
                if (editTextField.isVisible()) {
                    editTextField.requestFocus(); // Set focus to the text field
                } else {
                    // Update the comment content in the database when the user confirms the edit
                    String updatedContent = editTextField.getText();
                    commentService.updateComment(comment.getId(), updatedContent);
                    // Update the label with the new content
                    commentContentLabel.setText(updatedContent);
                }
            });

            deleteButton.setOnAction(event -> deleteComment(comment, commentsContainer, commentBox));
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
            editTextField.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    updateComment(comment, editTextField, commentContentLabel);
                }
            });
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
    }


    private void toggleEdit(Comment comment, Label commentContentLabel, TextField editTextField) {
        boolean isVisible = editTextField.isVisible();
        commentContentLabel.setVisible(!isVisible);
        editTextField.setVisible(isVisible);
        if (isVisible) {
            editTextField.requestFocus();
        }
    }

    private void updateComment(Comment comment, TextField editTextField, Label commentContentLabel) {
        String updatedContent = editTextField.getText();
        commentService.updateComment(comment.getId(), updatedContent);
        commentContentLabel.setText(updatedContent);
        commentContentLabel.setVisible(true);
        editTextField.setVisible(false);
    }

    private void deleteComment(Comment comment, VBox container, HBox commentBox) {
        commentService.deleteComment(comment.getId());
        container.getChildren().remove(commentBox);
    }

    @FXML
    private void submitComment() {
        if (selectedPublication != null && !commentTextArea.getText().isEmpty()) {
            user user = getCurrentUser();
            Comment newComment = new Comment(commentTextArea.getText(), selectedPublication, user);
            commentService.addComment(newComment);
            commentTextArea.clear();
            loadComments();
        } else {
            showAlert("Please enter a comment or select a publication.");
        }
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
