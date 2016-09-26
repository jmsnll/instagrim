package uk.ac.dundee.computing.tjn.instagrim.stores;

public class LoggedIn {

    private boolean loggedIn = false;
    private String username = null;

    public void Logged() {

    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
