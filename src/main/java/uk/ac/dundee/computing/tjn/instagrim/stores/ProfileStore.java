package uk.ac.dundee.computing.tjn.instagrim.stores;

import java.util.UUID;
import uk.ac.dundee.computing.tjn.instagrim.models.UserModel;

public class ProfileStore {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private String bio;
    private UUID profilePic;

    public ProfileStore() {

    }

    public ProfileStore(UserModel user) {
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.emailVerified = user.getEmailVerified();
        this.bio = user.getBio();
        this.profilePic = user.getProfilePic();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UUID getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(UUID profilePic) {
        this.profilePic = profilePic;
    }
}
