package uk.ac.dundee.computing.tjn.instagrim.stores;

import uk.ac.dundee.computing.tjn.instagrim.models.UserModel;

/**
 *
 * @author James Neill
 */
public class ProfileStore {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private String bio;
//    private UUID profilePic;

    /**
     *
     */
    public ProfileStore() {

    }

    /**
     *
     * @param user
     */
    public ProfileStore(UserModel user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.emailVerified = user.getEmailVerified();
        this.bio = user.getBio();
    }

    /**
     * Gets the username
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the usernames
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the first name
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the last name
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the last name
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the email
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Checks if the email address has been verified
     *
     * @return
     */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * Sets if the email address has been verified
     *
     * @param emailVerified
     */
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     * Gets the user's bio
     *
     * @return
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the user's bio
     *
     * @param bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }
}
