package sample;

import entite.formation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.formationService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class EditFormationController {

    @FXML
    private TextField nomFormationField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField formateurField;

    @FXML
    private TextField lieuField;
    private String imagePath;


    private formation formation;
    public void initialize() {
        // Initialisez formationService ici
        formationService = new formationService();
    }
    private formationService formationService;

    public void initData(formation formation) {
        this.formation = formation;
        nomFormationField.setText(formation.getNomFormation());
        descriptionField.setText(formation.getDescriptionFormation());

        LocalDate date = formation.getDateFormation();
        String dateString = date.toString();
        datePicker.setValue(LocalDate.parse(dateString));
        String imagePath = formation.getImageFormation();
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            }
        }



        formateurField.setText(formation.getFormateur());
        lieuField.setText(formation.getLieuFormation());
    }

    @FXML
    private void saveFormation() {
        formation.setNomFormation(nomFormationField.getText());
        formation.setDescriptionFormation(descriptionField.getText());
        formation.setDateFormation( datePicker.getValue());
        formation.setFormateur(formateurField.getText());
        formation.setLieuFormation(lieuField.getText());
        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals(formation.getImageFormation())) {

            formation.setImageFormation(imagePath);}


        try {
            formationService.updateFormation(formation);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/afficherformation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("List formations");
            stage.show();
            closeStage();
;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    private ImageView imageView;

    // Définissez une méthode pour charger et afficher l'image
    @FXML
    private void loadImage() {
        // Créez un sélecteur de fichier pour choisir une image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File selectedFile = fileChooser.showOpenDialog(null);

        // Si un fichier est sélectionné, chargez-le dans l'ImageView et stockez le chemin du fichier
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }
    @FXML
    private void cancel() {
        try {
            // Load the previous page (you might need to adjust the file path)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("formation/afficherformation.fxml"));
            Parent root = loader.load();

            // Get the current stage from a component
            Stage stage = (Stage) nomFormationField.getScene().getWindow();

            // Set the scene for the stage to the previous page
            stage.setScene(new Scene(root));
            stage.setTitle("List formations"); // You can set a relevant title for your previous page
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as you see fit
        }
    }


    private void closeStage() {
        Stage stage = (Stage) nomFormationField.getScene().getWindow();
        stage.close();
    }
}
