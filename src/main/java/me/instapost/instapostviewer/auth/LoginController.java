package me.instapost.instapostviewer.auth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.instapost.instapostviewer.manager.User;
import me.instapost.instapostviewer.InstaPostViewer;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class LoginController {
    public Scene scene;
    public Stage stage;
    @FXML
    public Button closeBtn;
    @FXML
    public Button loginBtn;
    @FXML
    public ImageView logo;
    @FXML
    public Pane navbar;
    @FXML
    public ImageView raulogoImg;
    @FXML
    public TextField emailTF;
    @FXML
    public PasswordField passTF;

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
        InstaPostViewer.changeScene("register.fxml");
    }
    @FXML
    public void login() {
        String password = passTF.getText();
        String email = emailTF.getText().toLowerCase();
        ///////////////////////////////////////////////
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
        User USER = new User(email, password);
        if (!InstaPostViewer.instance.USERS.exists(USER)) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Login error","User with the email address '" + email + "' does not exist.");
            return;
        }
        if (!InstaPostViewer.instance.USERS.canLogin(USER)) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Login error","Wrong password, try again later.");
            return;
        }
        InstaPostViewer.instance.loggedUser = USER;
        InstaPostViewer.changeScene("main.fxml", 1024, 768);
        InstaPostViewer.showAlert(Alert.AlertType.CONFIRMATION, "You are logged in", "Successufully logged in in account with the email '" + email + "', now you can use the application! :)");
    }
    @FXML
    public void closeEnter() {
        this.closeBtn.setStyle("-fx-background-color: red;");
    }
    @FXML
    public void closeExit() {
        this.closeBtn.setStyle("-fx-background-color: #BD081C;");
    }
    @FXML
    public void loginEnter() {
        this.loginBtn.setCursor(Cursor.HAND);
        this.loginBtn.setStyle("-fx-background-color: #9e418d; -fx-text-fill: white;");
    }
    @FXML
    public void loginExit() {
        this.loginBtn.setCursor(Cursor.DEFAULT);
        this.loginBtn.setStyle("-fx-background-color: black;");
    }
    @FXML
    protected void initialize() {
        Platform.runLater(() -> {
            this.scene = this.navbar.getScene();
            this.stage = (Stage) this.scene.getWindow();
            if (this.scene == null) {
                return;
            }
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);
            this.navbar.setOnMousePressed(event -> {
                xOffset.set(event.getSceneX());
                yOffset.set(event.getSceneY());
            });
            this.navbar.setOnMouseDragged(event -> {
                this.stage.setX(event.getScreenX() - xOffset.get());
                this.stage.setY(event.getScreenY() - yOffset.get());
            });
            String path = "/me/instapost/instapostviewer/images/";
            String log = LoginController.class.getResource(path + "logo.png").toString();
            String rauLogo = LoginController.class.getResource(path +  "logo-RAU-ro-1.png").toString();
            this.logo.setImage(new Image(log));
            this.raulogoImg.setImage(new Image(rauLogo));
        });
    }
}
