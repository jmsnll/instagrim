package uk.ac.dundee.computing.tjn.instagrim.stores;

/**
 *
 * @author James Neill
 */
public class SessionStore {

    private boolean loggedIn = false;
    private String username = null;

    /**
     *
     */
    public SessionStore() {

    }

    /**
     * Checks if a user is logged in or not
     *
     * @return
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Sets if a user is logged in or not
     *
     * @param loggedIn
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * Gets the username of the currently logged in user
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the currently logged in user
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
