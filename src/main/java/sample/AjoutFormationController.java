package sample;

import entite.formation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.formationService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import static sample.SessionManager.getCurrentUserId;

public class AjoutFormationController {

    @FXML
    private TextField nomFormationField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField dateField;

    @FXML
    private TextField formateurField;

    @FXML
    private TextField lieuField;

    @FXML
    private Button ajouterButton;
    @FXML
    private Button backButton;

    @FXML
    private ImageView imageView;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label nomErrorLabel;
    @FXML
    private Label descriptionErrorLabel;
    @FXML
    private Label dateErrorLabel;
    @FXML
    private Label formateurErrorLabel;
    @FXML
    private Label lieuErrorLabel;


    private formationService formationService;

    public AjoutFormationController() {
        formationService = new formationService();
    }

    @FXML
    private void ajouterFormation() {
        LocalDate currentDate = LocalDate.now();
        nomErrorLabel.setText("");
        descriptionErrorLabel.setText("");
        dateErrorLabel.setText("");
        formateurErrorLabel.setText("");
        lieuErrorLabel.setText("");

        String nomFormation = nomFormationField.getText();
        String description = descriptionField.getText();
        LocalDate date = datePicker.getValue();
        String formateur = formateurField.getText();
        String lieu = lieuField.getText();

        // Obtenir le chemin de l'image
        String imagePath = null;
        if (imageView.getImage() != null) {
            imagePath = imageView.getImage().getUrl();
            if (imagePath.startsWith("file:/")) {
                imagePath = imagePath.substring(6); // Supprime le préfixe "file:/"
            }
        } if (nomFormation.isEmpty()) {
            nomErrorLabel.setText("Nom de la formation requis");
            return; // Arrêter l'ajout si le champ est vide
        }
        if (nomFormation.length() < 3 || nomFormation.length() > 15) {
            nomErrorLabel.setText("Le nom doit contenir entre 3 et 15 caractères");
            return;
        }
        if (!Pattern.matches("^(?=.*[a-zA-Z])[a-zA-Z\\d ]+$", nomFormation)) {
            nomErrorLabel.setText("Le nom de la formation doit contenir au moins une lettre, pas seulement des chiffres et pas de caractères spéciaux");
            return;
        }
        if (!formationService.checkFormationNameUniqueness(nomFormation)) {
            nomErrorLabel.setText ("Erreur de saisie nom doit etre unique");
            return; // Arrêtez l'ajout si le nom de la formation n'est pas unique
        }
        if (description.isEmpty()) {
            descriptionErrorLabel.setText("Description requise");
            return; // Arrêter l'ajout si le champ est vide
        }
        if (description.length() < 20) {
            descriptionErrorLabel.setText("La description doit contenir au moins 20 caractères");
            return;
        }


        if (formateur.isEmpty()) {
            formateurErrorLabel.setText("Formateur requis");
            return; // Arrêter l'ajout si le champ est vide
        }
        if (formateur.length() < 3 || nomFormation.length() > 15) {
            formateurErrorLabel.setText("Le nomFormateur doit contenir entre 3 et 15 caractères");
            return;
        }
        if (date == null) {
            dateErrorLabel.setText("Date requise");
            return; // Arrêter l'ajout si le champ est vide
        }

        if (date.isBefore(currentDate)) {
            // Afficher un message d'erreur si la date est antérieure à la date actuelle
            dateErrorLabel.setText("La date doit être postérieure à la date actuelle");
            return;
        }


        if (lieu.isEmpty()) {
            lieuErrorLabel.setText("Lieu requis");
            return; // Arrêter l'ajout si le champ est vide
        }
        if (lieu.length() < 3 || nomFormation.length() > 15) {
            lieuErrorLabel.setText("Le nomFormateur doit contenir entre 3 et 15 caractères");
            return;
        }
        {
            try {
                formation nouvelleFormation = new formation(0,nomFormation, description, date, formateur, lieu, imagePath);
                nouvelleFormation.setFormationuser_id(getCurrentUserId());
                formationService.addFormation(nouvelleFormation);
                System.out.println("Formation ajoutée avec succès !");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/afficherformation.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("List formations");
                stage.show();
                ajouterButton.getScene().getWindow().hide();


            } catch (SQLException e) {
                System.err.println("Erreur lors de l'ajout de la formation : " + e.getMessage());
                // Gérer l'erreur, afficher un message d'erreur, journaliser l'erreur, etc.
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
    void handleUploadButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // Convert file to URL and then get the external form
                String imageUrl = file.toURI().toURL().toExternalForm();

                // Load the image using the URL
                Image image = new Image(imageUrl);
                imageView.setImage(image);
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
                // Handle the error
            }
        }
    }

}
