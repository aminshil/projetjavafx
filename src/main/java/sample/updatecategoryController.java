package sample;

import entite.Category;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import service.CategoryService;

import java.sql.SQLException;

public class updatecategoryController {

    private final CategoryService rs = new CategoryService();
    @FXML
    private TextField typecat;

    public updatecategoryController() throws SQLException {
    }


    @FXML
    void submitupdate() {
        affichercatController.category.setTypeofcat(typecat.getText());

        rs.update(affichercatController.category);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText("category Modifier avec succés");
        alert.showAndWait();

    }
    public void initData (Category cat){

        // Populate the form fields with the data of the selected Dons object
        typecat.setText(cat.getTypeofcat());

        //image.setText(don.getImage());

    }
}


