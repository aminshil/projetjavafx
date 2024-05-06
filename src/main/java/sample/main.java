package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import static sample.SessionManager.*;

public class main {
    @FXML
    private Button authButton;
    @FXML
    private void authentification() {
        try {
            System.out.println("auth");
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) authButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openlistpubuser() {
        if (getCurrentUser() == null){
            try {
                System.out.println("auth");
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) authButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
        try {
            System.out.println("openlistpubuser");
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("publication/liste.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) authButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }}
    }

    public void openlistformationuser() {
        if (getCurrentUser() == null){
            try {
                System.out.println("auth");
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) authButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                System.out.println("openlistformationuser");
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("formation/afficherformation.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) authButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }}
    }


    public void openlistdemuser() {
        if (getCurrentUser() == null){
            try {
                System.out.println("auth");
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) authButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
        try {
            System.out.println("openlistdemuser");
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("publication/listedem.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) authButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }}




}
