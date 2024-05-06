package sample;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ChatBotController {

    @FXML
    private TextField inputTextField;

    @FXML
    private TextArea outputLabel;

    @FXML
    private TextArea chatArea;

    private static final String NODE_SERVER = "http://localhost:3000/api/";

    @FXML
    private void processInput() {
        String input = inputTextField.getText();
        if (!input.isEmpty()) {
            addMessage("Me", input);
            inputTextField.clear();
            String response = getGeminiAPIAnswer(input); // Replace with your API call
            addMessage("Eco partage AI", response);
        }
    }

    private String getGeminiAPIAnswer(String input) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(NODE_SERVER + input)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                return responseBody;
            } else {
                return "Oops! An error occurred while fetching the answer. Response code: " + response.code();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Oops! An error occurred while processing your request. Error: " + e.getMessage();
        }
    }

    @FXML
    void Goback(ActionEvent event) {
        try {
            // Load the Ajouter interface FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterMessage.fxml"));
            Parent ajouterInterface = loader.load();

            // Create a new scene
            Scene ajouterScene = new Scene(ajouterInterface);

            // Get the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene and show the stage
            currentStage.setScene(ajouterScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception properly in your application
        }
    }

    private void addMessage(String sender, String message) {
        chatArea.appendText(sender + ": " + message + "\n");
    }


}
