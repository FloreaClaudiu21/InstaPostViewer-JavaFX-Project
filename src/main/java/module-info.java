module com.example.ytvideoplayerjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires com.google.gson;
    opens me.instapost.instapostviewer to javafx.fxml;
    exports me.instapost.instapostviewer;
    exports me.instapost.instapostviewer.auth;
    opens me.instapost.instapostviewer.auth to javafx.fxml;
    exports me.instapost.instapostviewer.manager;
    opens me.instapost.instapostviewer.manager to javafx.fxml;
    exports me.instapost.instapostviewer.main;
    opens me.instapost.instapostviewer.main to javafx.fxml;
}