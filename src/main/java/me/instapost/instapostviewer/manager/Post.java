package me.instapost.instapostviewer.manager;

public class Post {
    public String user;
    public String username;
    public String category;
    public boolean verified;
    public boolean Private;
    public String userImage;
    public int followers;
    public String bio;
    public String postURL;
    public int likes;
    public String location;
    public String summary;
    public String publishedAt;

    public Post(String user, String username, String category, String userImage, String bio, String postURL, String loc, String summary, String published, int followers, int likes, boolean verify, boolean privat) {
        this.bio = bio;
        this.user = user;
        this.likes = likes;
        this.location = loc;
        this.postURL = postURL;
        this.summary = summary;
        this.username = username;
        this.userImage = userImage;
        this.followers = followers;
        this.publishedAt = published;
        this.Private = privat;
        this.verified = verify;
        this.category = category;
    }
}
