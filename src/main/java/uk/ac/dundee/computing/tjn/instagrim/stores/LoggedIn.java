package uk.ac.dundee.computing.tjn.instagrim.stores;

public class LoggedIn {

    boolean logedin = false;
    String username = null;

    public void LogedIn() {

    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getUsername() {
        return username;
    }

    public void setLogedin() {
        logedin = true;
    }

    public void setLogedout() {
        logedin = false;
    }

    public void setLoginState(boolean logedin) {
        this.logedin = logedin;
    }

    public boolean getlogedin() {
        return logedin;
    }
}
