package me.instapost.instapostviewer.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.instapost.instapostviewer.InstaPostViewer;
import me.instapost.instapostviewer.auth.RegisterController;
import me.instapost.instapostviewer.manager.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class MainController {
    public Scene scene;
    public Stage stage;
    @FXML
    public Button closeBtn;
    @FXML
    public Button minBtn;
    @FXML
    public Pane navbar;
    @FXML
    public ImageView logo;
    @FXML
    public ImageView imgGalery;
    @FXML
    public ImageView infoImg;
    @FXML
    public ImageView userImg;
    @FXML
    public TextField searchTB;
    private String lastSearch = "";
    @FXML
    private Button searchBtn;
    @FXML
    private Pane loadingPanel;
    @FXML
    private Label usernameLbl;
    @FXML
    private Label progLbl;
    @FXML
    private Label followLbl;
    @FXML
    private Label likesLbl;
    public int startImage = 0;
    public int maxImage = 0;
    @FXML
    private ScrollPane infoPanel;
    /////////////////////////////
    @FXML
    private Label userLbl;
    @FXML
    private Label bioInfoLbl;
    @FXML
    private Label userInfoLbl;
    @FXML
    private Label usernameInfoLbl;
    @FXML
    private Label categoryInfoLbl;
    @FXML
    private Label createdInfoLbl;
    @FXML
    private Label privateInfoLbl;
    @FXML
    private Label verifiedInfoLbl;
    @FXML
    private Label summaryInfoLbl;
    @FXML
    private Label locInfoLbl;

    @FXML
    public void close() {
        Optional<ButtonType> result = InstaPostViewer.showConfirm("Exit the program", "Are you sure you want to close the application?");
        if (result.isPresent() && result.get().getText() == "Yes") {
            System.exit(0);
        }
    }
    @FXML
    public void minimize() {
        stage.setIconified(true);
    }
    @FXML
    public void press(KeyEvent ev) {
        KeyCode code = ev.getCode();
        if (code == KeyCode.ENTER) {
            search();
        }
    }
    @FXML
    public void toggleInfoPanel(MouseEvent ev) {
        Bloom bloom = new Bloom();
        bloom.setThreshold(0.2);
        ImageView iv = (ImageView) ev.getSource();
        boolean visible = infoPanel.isVisible();
        if (!iv.getId().equals("infoImg")) {
            infoImg.setEffect(null);
            infoPanel.setVisible(false);
            return;
        }
        if (!visible) {
            infoImg.setEffect(bloom);
        } else {
            infoImg.setEffect(null);
        }
        infoPanel.setVisible(!visible);
        stage.requestFocus();
    }
    private void updateInfoPanel(Post post) {
        infoPanel.setHvalue(0);
        bioInfoLbl.setText(post.bio);
        userInfoLbl.setText(post.user);
        locInfoLbl.setText(post.location);
        summaryInfoLbl.setText(post.summary);
        usernameInfoLbl.setText(post.username);
        categoryInfoLbl.setText(post.category);
        createdInfoLbl.setText(post.publishedAt);
        privateInfoLbl.setText(post.Private + "");
        verifiedInfoLbl.setText(post.verified + "");
    }
    @FXML
    public void next() {
        startImage += 1;
        if (startImage >= maxImage) {
            startImage = 0;
        }
        Post post = InstaPostViewer.POSTS.get(startImage);
        infoImg.setEffect(null);
        this.updateInfoPanel(post);
        imgGalery.setImage(new Image(post.postURL));
        progLbl.setText((startImage + 1) + "/" + maxImage);
        likesLbl.setText(InstaPostViewer.numberFormat((double)post.likes, 0) + "❤");
    }
    @FXML
    public void prev() {
        startImage -= 1;
        if (startImage < 0) {
            startImage = maxImage - 1;
        }
        Post post = InstaPostViewer.POSTS.get(startImage);
        infoImg.setEffect(null);
        this.updateInfoPanel(post);
        imgGalery.setImage(new Image(post.postURL));
        progLbl.setText((startImage + 1) + "/" + maxImage);
        likesLbl.setText(InstaPostViewer.numberFormat((double)post.likes, 0) + "❤");
    }
    @FXML
    public void search() {
        String name = searchTB.getText();
        /////////////////////////////////
        if (name == null || name.isEmpty()) {
            InstaPostViewer.showAlert(Alert.AlertType.ERROR, "Empty search box", "You must specify a username before starting to search!");
            return;
        }
        if (lastSearch.equalsIgnoreCase(name)) {
            return;
        }
        infoImg.setVisible(false);
        progLbl.setVisible(false);
        searchTB.setDisable(true);
        likesLbl.setVisible(false);
        searchBtn.setDisable(true);
        followLbl.setVisible(false);
        infoPanel.setVisible(false);
        loadingPanel.setVisible(true);
        Thread th = new Thread(() -> {
            List<Post> posts = InstaPostViewer.fetchPosts(name);
            if (posts == null || posts.isEmpty()) {
                lastSearch = name;
                infoImg.setVisible(true);
                progLbl.setVisible(true);
                likesLbl.setVisible(true);
                followLbl.setVisible(true);
                searchTB.setDisable(false);
                searchBtn.setDisable(false);
                loadingPanel.setVisible(false);
                Platform.runLater(() -> {
                    InstaPostViewer.showAlert(Alert.AlertType.INFORMATION, "Result of the searching", "No posts have been found for the user '" + name + "'.");
                });
                return;
            }
            startImage = 0;
            lastSearch = name;
            InstaPostViewer.POSTS = posts;
            maxImage = InstaPostViewer.POSTS.size();
            Post post = InstaPostViewer.POSTS.get(0);
            /////////////////////////////////////////
            progLbl.setVisible(true);
            infoImg.setVisible(true);
            likesLbl.setVisible(true);
            searchTB.setDisable(false);
            followLbl.setVisible(true);
            searchBtn.setDisable(false);
            loadingPanel.setVisible(false);
            Platform.runLater(() -> {
                usernameLbl.setText(name);
                this.updateInfoPanel(post);
                imgGalery.setImage(new Image(post.postURL));
                userImg.setImage(new Image(post.userImage));
                usernameLbl.setTooltip(new Tooltip(post.bio));
                progLbl.setText((startImage + 1) + "/" + maxImage);
                likesLbl.setText(InstaPostViewer.numberFormat((double)post.likes, 0) + "❤");
                followLbl.setText(InstaPostViewer.numberFormat((double)post.followers, 0) + " followers");
            });
        });
        th.start();
        InstaPostViewer.showAlert(Alert.AlertType.INFORMATION, "Started searching", "Started searching for instagram images from user '" + name + "'.");
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
    public void infoEnter() {
        this.infoImg.setCursor(Cursor.HAND);
    }
    @FXML
    public void infoExit() {
        this.infoImg.setCursor(Cursor.DEFAULT);
    }
    @FXML
    public void minEnter() {
        this.minBtn.setStyle("-fx-background-color: darkgray;");
    }
    @FXML
    public void minExit() {
        this.minBtn.setStyle("-fx-background-color: black;");
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
            String log = RegisterController.class.getResource(path + "logo.png").toString();
            this.logo.setImage(new Image(log));
            this.userLbl.setText(InstaPostViewer.instance.loggedUser.EMAIL);
            this.userLbl.setTooltip(new Tooltip(InstaPostViewer.instance.loggedUser.EMAIL));
            search();
        });
    }
}
