package me.instapost.instapostviewer;

import com.google.gson.*;
import javafx.application.Application;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.instapost.instapostviewer.manager.User;
import me.instapost.instapostviewer.manager.Users;
import me.instapost.instapostviewer.manager.Post;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class InstaPostViewer extends Application {
    public Users USERS;
    public User loggedUser;
    public static Stage STAGE;
    public static List<Post> POSTS;
    public static InstaPostViewer instance;

    public void start(Stage stage) throws Exception {
        instance = this;
        this.STAGE = stage;
        this.USERS = new Users();
        this.POSTS = new ArrayList<>();
        ///////////////////////////////
        stage.setResizable(false);
        changeScene("login.fxml");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("InstaPostViewer - Java FX");
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

    public static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(msg);
        alert.showAndWait();
    }
    public static void changeScene(String fxmlFile, int w, int h) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(InstaPostViewer.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load(), w, h);
            STAGE.setScene(scene);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    public static void changeScene(String fxmlFile) {
       changeScene(fxmlFile, 460, 600);
    }
    public static Optional<ButtonType> showConfirm(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(msg);
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    private static List<Post> extractData(String data) throws IOException {
        if (data == null || data.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();
        int followers = 0, likes = 0;
        boolean privat, verified = false;
        String pattern = "dd-MM-yyyy HH:mm";
        List<Post> posts = new ArrayList<>();
        String user = "", category = "None", location = "", created = "", summary = "", userName = "", biography = "", profilePic = "";
        JsonObject jsonObject = gson.fromJson(data.toString(), JsonObject.class);
        JsonElement errMsg = jsonObject.get("errorMessage");
        if (errMsg != null) {
            return null;
        }
        JsonElement userEL = jsonObject.get("username");
        if (userEL != null) {
            userName = userEL.getAsString();
        }
        JsonElement userFEL = jsonObject.get("full_name");
        if (userFEL != null) {
            user = userFEL.getAsString();
        }
        JsonElement biographyEL = jsonObject.get("biography");
        if (biographyEL != null) {
            biography = biographyEL.getAsString();
        }
        JsonElement catEL = jsonObject.get("category_name");
        if (catEL != null && (!(catEL instanceof JsonNull))) {
           category = catEL.getAsString();
        }
        privat = jsonObject.get("is_private").getAsBoolean();
        verified = jsonObject.get("is_verified").getAsBoolean();
        profilePic = jsonObject.get("profile_pic_url").getAsString();
        JsonObject followEl = jsonObject.getAsJsonObject("edge_followed_by");
        followers = followEl.get("count").getAsInt();
        JsonObject edges = jsonObject.getAsJsonObject("edge_owner_to_timeline_media");
        JsonArray postArray = edges.getAsJsonArray("edges");
        for (JsonElement nodeEL : postArray) {
            summary = "";
            JsonObject postObject = nodeEL.getAsJsonObject();
            JsonObject nodeElement = postObject.getAsJsonObject("node");
            JsonObject likesElement = nodeElement.getAsJsonObject("edge_liked_by");
            JsonElement locationElement = nodeElement.get("location");
            long takenElement = nodeElement.get("taken_at_timestamp").getAsLong();
            Date date = new Date(takenElement * 1000L);
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            created = dateFormat.format(date);
            JsonElement nameLocEl = null;
            if (locationElement != null && locationElement instanceof JsonObject) {
                nameLocEl = locationElement.getAsJsonObject().get("name");
            }
            if (nameLocEl != null) {
                location = nameLocEl.getAsString();
            } else {
                location = "N/A";
            }
            likes = likesElement.get("count").getAsInt();
            String postURL = nodeElement.get("display_url").getAsString();
            JsonObject edge_medias = nodeElement.getAsJsonObject("edge_media_to_caption");
            JsonArray edges_media = edge_medias.getAsJsonArray("edges");
            for (JsonElement edgenodeEL : edges_media) {
                JsonObject mediaObject = edgenodeEL.getAsJsonObject();
                JsonObject nodemediaEl = mediaObject.getAsJsonObject("node");
                JsonElement textEl = nodemediaEl.get("text");
                summary += (textEl.getAsString() + "\n");
            }
            if (summary.isEmpty()) {
               summary = "No summary";
            }
            boolean isVideo = nodeElement.get("is_video").getAsBoolean();
            if (!isVideo) {
                Post post = new Post(user, userName, category, profilePic, biography, postURL, location, summary, created, followers, likes, verified, privat);
                posts.add(post);
            }
        }
        return posts;
    }
    public static List<Post> fetchPosts(String username) {
        try {
            URL url = new URL("https://instagram-looter2.p.rapidapi.com/profile?username=" + username);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-RapidAPI-Key", "8a5919d1e8msh0f11ded308b5e20p1f15a9jsn8105705ff20e");
            con.setRequestProperty("X-RapidAPI-Host", "instagram-looter2.p.rapidapi.com");
            int status = con.getResponseCode();
            if (status != 200) {
                return null;
            }
            String inputLine;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            List<Post> posts = extractData(content.toString());
            in.close();
            con.disconnect();
            return posts;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static char[] c = new char[]{'k', 'm', 'b', 't'};
    public static String numberFormat(double n, int iteration) {
        if (n < 1000) {
            return (int)n + "";
        }
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;
        return (d < 1000?
                ((d > 99.9 || isRound || (!isRound && d > 9.99)?
                        (int) d * 10 / 10 : d + ""
                ) + "" + c[iteration])
                : numberFormat(d, iteration+1));
    }
    public static String weakPassword(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[!@#\\$%\\^&\\*].*")) {
            return "Password must contain a special character among !@#$%^&*";
        }
        if (!password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*")) {
            return "Password must contain at least one uppercase letter, one lowercase letter, and one digit.";
        }
        String[] commonWords = {"password", "123456", "qwerty", "letmein", "football", "monkey", "111111", "abc123", "welcome", "admin"};
        for (String word : commonWords) {
            if (password.toLowerCase().contains(word)) {
                return "Password cannot contain common words or phrases.";
            }
        }
        if (password.matches("(.)\\1{2,}")) {
            return "Password cannot contain repeated characters.";
        }
        return null;
    }
}