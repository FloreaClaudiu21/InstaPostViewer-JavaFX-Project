package me.instapost.instapostviewer.manager;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Users {
    public List<User> ACCOUNTS = new ArrayList<>();
    private static final File DB_FILE = new File("database.dat");

    public Users() throws Exception {
        String line = "";
        FileReader FR = new FileReader(DB_FILE);
        BufferedReader BFR = new BufferedReader(FR);
        while ((line = BFR.readLine()) != null) {
            if (!line.contains("/")) {
                continue;
            }
            String[] ST = line.split("/");
            ACCOUNTS.add(new User(ST[0].toLowerCase(Locale.ROOT), ST[1]));
        }
        BFR.close();
    }

    public boolean exists(User USER) {
        for (User ACCOUNT : ACCOUNTS) {
            if (ACCOUNT.EMAIL.equals(USER.EMAIL)) {
                return true;
            }
        }
        return false;
    }

    public boolean register(User USER) {
        try {
            FileWriter FW = new FileWriter(DB_FILE, true);
            BufferedWriter BW = new BufferedWriter(FW);
            BW.write("\n" + USER.toString());
            this.ACCOUNTS.add(USER);
            BW.close();
            FW.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public boolean canLogin(User USER) {
        if (!exists(USER)) return false;
        for (User ACCOUNT : ACCOUNTS) {
            if (ACCOUNT.tryLogin(USER)) {
                return true;
            }
        }
        return false;
    }
}
