package me.instapost.instapostviewer.manager;

public class User extends Object {
    public String EMAIL;
    private String PASSWORD;

    public User(String EMAIL, String PASSWORD) {
        this.EMAIL = EMAIL;
        this.PASSWORD = PASSWORD;
    }

    public boolean tryLogin(User USER) {
        if (USER.EMAIL.equals(this.EMAIL) && USER.PASSWORD.equals(this.PASSWORD)) {
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return this.EMAIL + "/" + this.PASSWORD;
    }
}
