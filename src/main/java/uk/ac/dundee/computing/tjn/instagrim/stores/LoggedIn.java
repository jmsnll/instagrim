package uk.ac.dundee.computing.tjn.instagrim.stores;

public class LoggedIn {

    boolean loggedIn = false;
    String username = null;

    public void LogedIn() {

    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getUsername() {
        return username;
    }

    public void setLoggedin() {
        loggedIn = true;
    }

    public void setLoggedout() {
        loggedIn = false;
    }

    public void setLoginState(boolean logedin) {
        this.loggedIn = logedin;
    }

    public boolean getlogedin() {
        return loggedIn;
    }
}
