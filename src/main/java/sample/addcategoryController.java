package sample;


import entite.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import service.CategoryService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class addcategoryController {
    private final CategoryService sd = new CategoryService();
    private boolean update;
    private int id;

    Category cat = null;
    ObservableList<Category> pubList = FXCollections.observableArrayList();
    @FXML
    private TextField typecat;

    public addcategoryController() throws SQLException {
    }


    // Method to handle the action when the user clicks the "Ajouter Publication" button
    @FXML
    void ajoutercatbtn(MouseEvent event) {
        if (update == false) {
            if (typecat.getText().isEmpty() ) {
                showAlert("Veuillez remplir tous les champs du formulaire.");
                return;
            }
            if (!typecat.getText().matches("^[a-zA-Z ]+$")) {
                showAlert("Le champ 'sujet' doit contenir uniquement des caractères alphabétiques.");
                return;
            }


            if(update==false){
                sd.add(new Category( typecat.getText()));
            }
            else{
                try {
                    Category cat = sd.findById(id);
                    cat.setTypeofcat( typecat.getText());

                    sd.update(cat);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setContentText("Publication Modifier avec succés");
                    alert.showAndWait();

                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    Logger.getLogger(afficherpubController.class.getName()).log(Level.SEVERE, null, e);
                }

            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("publication/afficheCategory.fxml"));
                Parent root = loader.load();
                affichercatController controller = loader.getController();
                controller.loadData(); // Reload data in the afficherpubController
                typecat.getScene().setRoot(root);
            } catch (IOException e) {
                showAlert("Error navigating to affichercat.fxml: " + e.getMessage());
                e.printStackTrace();
            }
/*
// Vérifier si une image a été sélectionnée
            if (selectedImageFile != null) {
                // Obtenir le nom de fichier de l'image
                String imageName = selectedImageFile.getName();

                // Ajouter un suffixe au chemin pour l'ajuster au format utilisé dans votre application web
                String destinationPath = "C:\\Users\\LENOVO\\OneDrive - ESPRIT\\Desktop\\p\\Dons\\public\\uploads\\" + imageName;
                String[] parts = destinationPath.split("\\\\");
                String adjustedPath = String.join("/", parts);

                // Copier l'image dans un répertoire de stockage local
                File destinationFile = new File(adjustedPath);
                try {
                    Files.copy(selectedImageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur lors de la copie de l'image.");
                    return;// Sortir de la méthode en cas d'erreur

                }
            }

 */

/*
            try {
// Assuming im is your Image object for the image displayed in DonImageView
                sd.add(new Publication(topicTextField.getText(), selectedImageFile.toURI().toString(),publicationTextField.getText()));
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } catch (NullPointerException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Null pointer exception occurred: " + e.getMessage());
                alert.showAndWait();
            }


 */
        } else {
            try {
                Category cat = sd.findById(id);
                cat.setTypeofcat(typecat.getText());




                sd.update(cat);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setContentText("category Modifier avec succés");
                alert.showAndWait();

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                Logger.getLogger(affichercatController.class.getName()).log(Level.SEVERE, null, e);
            }

        }
        /*
      try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dons/AfficherDons.fxml"));
            Parent root = loader.load();
            topicTextField.getScene().setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            Logger.getLogger(AfficherDonsController.class.getName()).log(Level.SEVERE, null, e);
        }

*/
    }
    void setUpdate ( boolean b){
        this.update = b;

    }

    private void showAlert (String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    /*void selectImage (ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        selectedImageFile = fileChooser.showOpenDialog(null);
        if (selectedImageFile != null) {
            System.out.println("Fichier sélectionné : " + selectedImageFile.getName());
            // Vous pouvez également copier le fichier sélectionné dans un répertoire de stockage local ici

            // Afficher l'image sélectionnée dans une imageview
            Image image = new Image(selectedImageFile.toURI().toString());
            DonImageView.setImage(image); // Suppose que vous avez une ImageView appelée imageView dans votre interface FXML
        }
    }
*/

    public void initData (Category cat){

        // Populate the form fields with the data of the selected Dons object
        typecat.setText(cat.getTypeofcat());


    }
}

