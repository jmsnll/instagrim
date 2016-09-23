package uk.ac.dundee.computing.aec.instagrim.stores;

public class LoggedIn {

    boolean logedin = false;
    String Username = null;

    public void LogedIn() {

    }

    public void setUsername(String name) {
        this.Username = name;
    }

    public String getUsername() {
        return Username;
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
