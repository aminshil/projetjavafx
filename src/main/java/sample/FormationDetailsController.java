package sample;

import entite.commentaireformation;
import entite.formation;
import entite.user;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import service.ParticipationFormationService;
import service.commentaireformationService;
import service.formationService;
import service.userService;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static sample.SessionManager.getCurrentUserId;
import static sample.SessionManager.getCurrentUserRole;

public class FormationDetailsController {

    @FXML
    private ImageView imageView;
    @FXML
    private ListView<Node> commentairesListView;

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private Label nomFormationLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label formateurLabel;

    @FXML
    private Label lieuLabel;

    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private Button editButton;
    @FXML
    private TextArea commentaireArea;
    @FXML
    private Button exportPdfButton;

    @FXML
    private Button participateButton;
    @FXML
    private Button unparticipateButton;  // Assume you have this button in your FXML

    private ParticipationFormationService participationService;

    private formation formation;
    private commentaireformationService commentaireformationService;

    public void initData(formation formation) {
        this.formation = formation;
        participationService = new ParticipationFormationService(); // Assume DBConnection is your database connection class
        updateParticipationButtonState();


        // Mettre à jour les champs avec les données de la formation
        nomFormationLabel.setText(formation.getNomFormation());
        descriptionLabel.setText(formation.getDescriptionFormation());
        dateLabel.setText(formation.getDateFormation().toString());
        formateurLabel.setText(formation.getFormateur());
        lieuLabel.setText(formation.getLieuFormation());
        String imageUrl = formation.getImageFormation();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                URL url = new URL("file:" + imageUrl);
                Image image = new Image(url.toExternalForm());
                imageView.setImage(image);
            } catch (MalformedURLException e) {
                System.err.println("Invalid image URL: " + imageUrl);
                e.printStackTrace();
            }
        }
        commentaireformationService = new commentaireformationService();
        afficherCommentaires();
        setButtonVisibility();
        displayQRCode();


    }

    public void setButtonVisibility() {
        // Assuming getCurrentUserRole is a method that returns the current user's role
        String role = getCurrentUserRole();  // You need to implement this method according to your application's user management system

        if ("Admin".equals(role)) {
            editButton.setVisible(true);
            deleteButton.setVisible(true);
        } else {
            editButton.setVisible(false);
            deleteButton.setVisible(false);
        }
    }

    @FXML
    private void ajouterCommentaire() {
        String commentaireText = commentaireArea.getText();
        if (!commentaireText.isEmpty()) {
            Timestamp date = Timestamp.valueOf(LocalDateTime.now());
            commentaireformation commentaire = new commentaireformation(0, formation.getId(), commentaireText, date, getCurrentUserId());
            commentaire.setCommentaireuserformation_id(getCurrentUserId());
            try {
                commentaireformationService.addCommentaire(commentaire);
                // Afficher un message de succès ou actualiser l'affichage des commentaires
                System.out.println("Commentaire ajouté avec succès !");
                // Actualiser l'affichage des commentaires
                afficherCommentaires(); // Appel de la méthode pour afficher les commentaires
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'ajout du commentaire : " + e.getMessage());
                // Gérer l'erreur
            }
        } else {
            // Afficher un message d'erreur si le champ de commentaire est vide
            System.err.println("Veuillez saisir un commentaire.");
        }
    }

    private void afficherCommentaires() {
        commentairesListView.getItems().clear(); // Clear the ListView before adding new comments
        try {
            List<commentaireformation> commentaires = commentaireformationService.getCommentairesForFormation(formation.getId());
            for (commentaireformation commentaire : commentaires) {
                userService userService = new userService();
                user user = userService.getUserById(commentaire.getCommentaireuserformation_id()); // Fetch the user details

                VBox card = new VBox(10); // Vertical box with spacing
                card.setPadding(new Insets(10)); // Padding around elements inside the card
                card.getStyleClass().add("comment-card"); // CSS class for styling
                Label userLabel = new Label(user.getNom() + " " + user.getPrenom() + ":");

                String contenuFiltre = BadWordsFilter.filterBadWords(commentaire.getContenu());
                Label contenuLabel = new Label(contenuFiltre);
                contenuLabel.setWrapText(true);
                contenuLabel.getStyleClass().add("comment-content");

                Label dateLabel = new Label("Posté le: " + commentaire.getDateCommentaire().toString());
                dateLabel.getStyleClass().add("comment-date");

                Button deleteButton = new Button("Supprimer");
                deleteButton.getStyleClass().add("delete-button");

                // Determine if the current user is the creator of the comment or an admin
                boolean isCurrentUser = commentaire.getCommentaireuserformation_id() == getCurrentUserId();
                boolean isAdmin = "Admin".equals(getCurrentUserRole());
                deleteButton.setVisible(isCurrentUser || isAdmin); // Show delete button if user is the creator or an admin

                deleteButton.setOnAction(event -> {
                    try {
                        commentaireformationService.deleteCommentaire(commentaire);
                        afficherCommentaires(); // Refresh the comments display after deletion
                    } catch (SQLException e) {
                        e.printStackTrace(); // Handle exception
                    }
                });

                card.getChildren().addAll(userLabel, contenuLabel, dateLabel, deleteButton);
                commentairesListView.getItems().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goToListFormation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/afficherformation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("List formations");
            stage.show();
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'exception
        }
    }

    @FXML
    private void editFormation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/editFormation.fxml"));
            Parent root = loader.load();

            // Passer la formation actuelle à la vue de modification
            EditFormationController editController = loader.getController();
            editController.initData(formation);

            // Afficher la scène de modification de la formation
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Formation");
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) editButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'exception
        }
    }

    @FXML
    private void deleteFormation() {
        // Assurez-vous que la formation n'est pas nulle
        if (formation != null) {
            // Ajoutez ici la logique pour supprimer la formation
            try {
                commentaireformationService.deleteCommentairesByFormationId(formation.getId());

                formationService.deleteFormation(formation);
                System.out.println("Formation supprimée avec succès !");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/afficherformation.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("List formations");
                stage.show();

                deleteButton.getScene().getWindow().hide();

            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression de la formation : " + e.getMessage());
                // Gérer l'erreur, afficher un message d'erreur, journaliser l'erreur, etc.
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("La formation est nulle. Impossible de supprimer.");
            // Afficher un message d'erreur ou prendre d'autres mesures si la formation est nulle
        }
    }

    @FXML
    private void exportFormationToPDF() {
        if (formation == null) {
            System.err.println("No formation data available.");
            return;
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Adding the formation's image
            if (formation.getImageFormation() != null && !formation.getImageFormation().isEmpty()) {
                String imageUrl = formation.getImageFormation(); // This should be a local path
                PDImageXObject pdImage = PDImageXObject.createFromFile(imageUrl, document);
                contentStream.drawImage(pdImage, 70, 650, 300, 150); // Larger size for better visibility
            } else {
                System.out.println("No image available for this formation.");
            }

            // Text content below the image
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_BOLD, 14);
            contentStream.setLeading(18.5f);
            contentStream.newLineAtOffset(50, 600);
            contentStream.showText("Formation Name: " + formation.getNomFormation());
            contentStream.newLine();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            contentStream.showText("Description: " + formation.getDescriptionFormation());
            contentStream.newLine();
            contentStream.showText("Date: " + formation.getDateFormation().toString());
            contentStream.newLine();
            contentStream.showText("Formateur: " + formation.getFormateur());
            contentStream.newLine();
            contentStream.showText("Lieu: " + formation.getLieuFormation());
            contentStream.endText();

            contentStream.close();

            String filename = "Formation_Details_" + formation.getId() + ".pdf";
            document.save(filename);
            System.out.println("PDF created: " + filename);
        } catch (Exception e) {
            System.err.println("Error creating PDF: " + e.getMessage());
        }
    }

    private void displayQRCode() {
        try {
            // Construct data string in a structured format
            StringBuilder dataBuilder = new StringBuilder();
            dataBuilder.append("Formation ID: ").append(formation.getId()).append("\n");
            dataBuilder.append("Name: ").append(formation.getNomFormation()).append("\n");
            dataBuilder.append("Description: ").append(formation.getDescriptionFormation()).append("\n");
            dataBuilder.append("Date: ").append(formation.getDateFormation().toString()).append("\n");
            dataBuilder.append("Formateur: ").append(formation.getFormateur()).append("\n");
            dataBuilder.append("Lieu: ").append(formation.getLieuFormation());

            // Convert the structured data to a byte array QR code
            byte[] qrCode = QRCodeGenerator.generateQRCodeImage(dataBuilder.toString(), 200, 200);

            // Convert the byte array into an Image object and set it to the ImageView
            ByteArrayInputStream bis = new ByteArrayInputStream(qrCode);
            Image image = SwingFXUtils.toFXImage(ImageIO.read(bis), null);
            qrCodeImageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();  // Handle exceptions appropriately
        }
    }


    @FXML
    private void handleParticipation() {
        try {
            participationService.registerParticipation(getCurrentUserId(), formation.getId());
            participateButton.setText("Participating");
            participateButton.setDisable(true);
            unparticipateButton.setDisable(false);
        } catch (SQLException e) {
            System.err.println("Error registering participation: " + e.getMessage());
        }
    }

    @FXML
    private void handleUnparticipation() {
        try {
            participationService.cancelParticipation(getCurrentUserId(), formation.getId());
            participateButton.setText("Participate");
            participateButton.setDisable(false);
            unparticipateButton.setDisable(true);
        } catch (SQLException e) {
            System.err.println("Error canceling participation: " + e.getMessage());
        }
    }

    private void updateParticipationButtonState() {
        try {
            boolean isParticipating = participationService.isUserParticipating(getCurrentUserId(), formation.getId());
            if (isParticipating) {
                participateButton.setText("Participating");
                participateButton.setDisable(true);
                unparticipateButton.setDisable(false);
            } else {
                participateButton.setText("Participate");
                participateButton.setDisable(false);
                unparticipateButton.setDisable(true);
            }
        } catch (SQLException e) {
            System.err.println("Error checking participation status: " + e.getMessage());
        }

    }
}


