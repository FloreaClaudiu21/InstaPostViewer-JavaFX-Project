package me.instapost.instapostviewer.auth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.instapost.instapostviewer.manager.User;
import me.instapost.instapostviewer.InstaPostViewer;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterController {
    public Scene scene;
    public Stage stage;
    @FXML
    public Button closeBtn;
    @FXML
    public Pane mainPanel;
    @FXML
    public Button registerBtn;
    @FXML
    public TextField emailTF;
    @FXML
    public TextField passTF;
    @FXML
    public TextField repassTF;

    @FXML
    public void close() {
        Optional<ButtonType> result = InstaPostViewer.showConfirm("Exit the program", "Are you sure you want to close the application?");
        if (result.isPresent() && result.get().getText() == "Yes") {
            System.exit(0);
        }
    }
    @FXML
    public void switchScene() {
        passTF.setText("");
        emailTF.setText("");
        repassTF.setText("");
        InstaPostViewer.changeScene("login.fxml");
    }
    @FXML
    public void register() {
        String password = passTF.getText();
        String repassword = repassTF.getText();
        String email = emailTF.getText().toLowerCase();
        ///////////////////////////////////////////////
        String weakP = InstaPostViewer.weakPassword(password);
        String weakP1 = InstaPostViewer.weakPassword(repassword);
        if (email == null || email.isEmpty()) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Field error","You must specify the email address");
            return;
        }
        if (!InstaPostViewer.isValidEmailAddress(email)) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Field error","Invalid email address! Please specify the correct email!");
            return;
        }
        if (password == null || password.isEmpty()) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Field error","You must specify the password");
            return;
        }
        if (repassword == null || repassword.isEmpty()) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Field error","You must specify the password");
            return;
        }
        if (weakP != null) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Register error", weakP);
            return;
        }
        if (weakP1 != null) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Register error", weakP1);
            return;
        }
        if (!password.equals(repassword)) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Register error","Passwords must match");
            return;
        }
        User USER = new User(email, password);
        if (InstaPostViewer.instance.USERS.exists(USER)) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Register error","User with the email address '" + email + "' already exists.");
            return;
        }
        InstaPostViewer.instance.USERS.register(USER);
        InstaPostViewer.changeScene("login.fxml");
        InstaPostViewer.showAlert(Alert.AlertType.CONFIRMATION, "You are registered", "Successufully registered account with the email '" + email + "', you can login now.");
    }
    @FXML
    public void closeEnter() {
        this.closeBtn.setStyle("-fx-background-color: red;-fx-background-radius: 20px;-fx-text-fill: yellow;");
    }
    @FXML
    public void closeExit() {
        this.closeBtn.setStyle("-fx-background-color: #BD081C;-fx-background-radius: 20px;-fx-text-fill: white;");
    }
    @FXML
    public void registerEnter() {
        this.registerBtn.setCursor(Cursor.HAND);
        this.registerBtn.setStyle("-fx-background-color: #9e418d; -fx-text-fill: white;");
    }
    @FXML
    public void registerExit() {
        this.registerBtn.setCursor(Cursor.DEFAULT);
        this.registerBtn.setStyle("-fx-background-color: black;");
    }
    @FXML
    protected void initialize() {
        Platform.runLater(() -> {
            this.scene = this.mainPanel.getScene();
            this.stage = (Stage) this.scene.getWindow();
            if (this.scene == null) {
                return;
            }
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
            this.mainPanel.setOnMousePressed(event -> {
                xOffset.set(event.getSceneX());
                yOffset.set(event.getSceneY());
            });
            this.mainPanel.setOnMouseDragged(event -> {
                this.stage.setX(event.getScreenX() - xOffset.get());
                this.stage.setY(event.getScreenY() - yOffset.get());
            });
        });
    }
}
