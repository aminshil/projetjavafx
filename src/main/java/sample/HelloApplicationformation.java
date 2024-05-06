package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplicationformation extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplicationformation.class.getResource("publication/ChatBot.fxml"));
        Parent root = fxmlLoader.load();
        double width = root.prefWidth(-1);
        double height = root.prefHeight(-1);

        Scene scene = new Scene(root, width, height);
        stage.setTitle("List formations ");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}