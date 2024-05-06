package sample;

import entite.user;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.util.Random;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.userService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

public class SignController {

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_prenom;

    @FXML
    private TextField tf_emailS;

    @FXML
    private PasswordField tf_passwordS;

    @FXML
    private Label labelError_nom;

    @FXML
    private Label labelError_prenom;

    @FXML
    private Label labelError_email;

    @FXML
    private Label labelError_password;

    @FXML
    private Button button_signn;


    private userService userService;
    @FXML
    private DatePicker dp_dateNaissance;
    @FXML
    private Label labelError_dateNaissance;
    private String imagePath = null; // Default image path
    @FXML private TextField  tf_captcha;
    @FXML private Label labelError_captcha;
    @FXML private Label captchaLabel;
    // Other fields...
    private String currentCaptcha;

    @FXML
    public void initialize() {
        generateCaptcha();
    }
    private void generateCaptcha() {
        currentCaptcha = "";
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            int ascii = rand.nextBoolean() ? 65 + rand.nextInt(26) : 97 + rand.nextInt(26);
            currentCaptcha += (char) ascii;
        }
        captchaLabel.setText(currentCaptcha);
    }

    public SignController() throws SQLException {
        userService = new userService();
    }

    @FXML
    private void signUp() {
        String captchaInput = tf_captcha.getText();
        String nom = tf_nom.getText();
        String prenom = tf_prenom.getText();
        String email = tf_emailS.getText();
        String password = tf_passwordS.getText();
        LocalDate dateNaissance = dp_dateNaissance.getValue();
        // Validation logic...
        boolean isValid = true;
        if (!captchaInput.equals(currentCaptcha)) {
            labelError_captcha.setText("Captcha is incorrect.");
            generateCaptcha(); // regenerate captcha
            return;
        }
        if (dateNaissance == null) {
            labelError_dateNaissance.setText("Date of birth is required.");
            return; // Ensure date is picked
        }
        if (nom.isEmpty()) {
            labelError_nom.setText("Le nom est requis.");
            isValid = false;
        } else if (!isValidName(nom)) {
            labelError_nom.setText("Le nom doit contenir uniquement des lettres.");
            isValid = false;
        } else {
            labelError_nom.setText("");
        }

        if (prenom.isEmpty()) {
            labelError_prenom.setText("Le prénom est requis.");
            isValid = false;
        } else if (!isValidName(prenom)) {
            labelError_prenom.setText("Le prénom doit contenir uniquement des lettres.");
            isValid = false;
        } else {
            labelError_prenom.setText("");
        }

        if (email.isEmpty()) {
            labelError_email.setText("L'email est requis.");
            isValid = false;
        } else if (!isValidEmail(email)) {
            labelError_email.setText("L'email n'est pas valide.");
            isValid = false;
        } else {
            labelError_email.setText("");
        }

        if (password.isEmpty()) {
            labelError_password.setText("Le mot de passe est requis.");
            isValid = false;
        } else {
            // Vérifier si le mot de passe contient au moins une lettre majuscule
            if (!hasUppercaseLetter(password)) {
                labelError_password.setText("Le mot de passe doit contenir au moins une lettre majuscule.");
                isValid = false;
            } else {
                labelError_password.setText("");
            }
        }

        if (isValid) {
            try {
                // Hash the password using SHA-256
                String hashedPassword = hashPassword(password);
                String role = "User";


                // Create a new user object with the retrieved details
                user newUser = new user(0, nom, prenom, email, hashedPassword, role, dateNaissance, imagePath);

                // Add the user using userService
                userService.addUser(newUser);

                System.out.println("User registered successfully!");
                // Optionally, you can navigate to another page or show a success message
            } catch (SQLException e) {
                System.err.println("Error occurred while registering user: " + e.getMessage());
                // Handle the error (show error message, log it, etc.)
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    private boolean hasUppercaseLetter(String password) {
        return !password.equals(password.toLowerCase());
    }

    @FXML
    void goToSign() {
        try {
            // Load the FXML document for sign-up
            Object root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello.fxml")));

            // Create a new scene and stage
            Scene scene = new Scene((Parent) root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.show();

            // Close the current window (login window)
            Stage currentStage = (Stage) button_signn.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions
        }
    }

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imagePath = file.getAbsolutePath();
        }

    }
}
