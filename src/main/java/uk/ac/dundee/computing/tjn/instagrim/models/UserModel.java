package uk.ac.dundee.computing.tjn.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.lib.PasswordStorage;
import uk.ac.dundee.computing.tjn.instagrim.lib.TwoFactorAuthUtil;

/**
 *
 * @author James Neill
 */
public class UserModel {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    private String base32secret;
    private Set<String> followers;
    private Set<String> following;
    private String bio;
    private ByteBuffer image = null;
    private int length;
    private String type;

    private final Cluster cluster;

    private TwoFactorAuthUtil twoFactorHandler = new TwoFactorAuthUtil();

    /**
     *
     * @param username
     * @param cluster
     */
    public UserModel(String username, Cluster cluster) {
        this.username = username;
        this.cluster = cluster;
        pull();
    }

    public ByteBuffer getImage() {
        return image;
    }

    public void setImage(ByteBuffer image) {
        this.image = image;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @param username
     * @param password
     * @param email
     * @param first_name
     * @param last_name
     * @param cluster
     */
    public UserModel(String username, String password, String email, String first_name, String last_name, Cluster cluster) {
        this.username = username;
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.email = email;
        this.firstName = first_name;
        this.lastName = last_name;
        this.base32secret = null;
        this.cluster = cluster;
    }

    /**
     *
     */
    public void pull() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT * FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        bs.bind(this.username);
        ResultSet rs = session.execute(bs);

        for (Row row : rs) {
            this.password = row.getString("password");
            this.firstName = row.getString("first_name");
            this.lastName = row.getString("last_name");
            this.email = row.getString("email");
            this.emailVerified = row.getBool("email_verified");
            this.base32secret = row.getString("base32secret");
            this.bio = row.getString("bio");
            this.following = row.getSet("following", String.class);
            this.followers = row.getSet("followers", String.class);
            this.image = row.getBytes("profile_pic");
        }
    }

    /**
     *
     */
    public void push() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("UPDATE instagrim.accounts SET password = ?, "
                + "first_name = ?, last_name = ?, email = ?, emailed_verified = ?, base32secret = ?,"
                + " bio = ?, following = ?, followers = ? WHERE username = ?;");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(this.password, this.firstName, this.lastName, this.email, this.emailVerified, this.base32secret, this.bio, this.following, this.followers, this.username));
    }

    /**
     *
     * @return
     */
    public boolean isTwoFactorEnabled() {
        if (this.base32secret == null) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    public void enableTwoFactor() {
        this.base32secret = twoFactorHandler.generateBase32Secret();
    }

    /**
     *
     */
    public void disableTwoFactor() {
        this.base32secret = null;
    }

    /**
     *
     * @return
     */
    public String getCurrentTwoFactorCode() {
        if (!isTwoFactorEnabled()) {
            return null;
        }
        String code = null;
        try {
            code = twoFactorHandler.generateCurrentNumber(this.base32secret);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return code;
    }

    /**
     *
     * @param code
     *
     * @return
     */
    public Boolean isValidTwoFactorCode(String code) {
        try {
            if (code.equals(twoFactorHandler.generateCurrentNumber(this.base32secret))) {
                return true;
            }
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean register() {
        if (exists(username, cluster)) {
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("INSERT INTO accounts (username, password, first_name, last_name, email, email_verified) Values(?,?,?,?,?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(username, password, firstName, lastName, email, false));
        return true;
    }

    /**
     *
     * @return
     */
    public boolean delete() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("DELETE FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(this.username));
        return true;
    }

    /**
     *
     * @param username
     * @param password
     * @param cluster
     *
     * @return
     */
    public static boolean isValidUser(String username, String password, Cluster cluster) {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT password FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet rs = session.execute(bs.bind(username));
        if (rs.isExhausted()) {
            System.out.println("No accounts returned.");
            return false;
        } else {
            for (Row row : rs) {
                String storedPassword = row.getString("password");
                try {
                    if (PasswordStorage.verifyPassword(password, storedPassword)) {
                        return true;
                    }
                } catch (PasswordStorage.CannotPerformOperationException | PasswordStorage.InvalidHashException ex) {
                    Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    /**
     *
     * @param username
     * @param cluster
     *
     * @return
     */
    public static boolean exists(String username, Cluster cluster) {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT username FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet rs = session.execute(bs.bind(username));
        if (rs.isExhausted()) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param username
     *
     * @return
     */
    public boolean follows(String username) {
        if (following.contains(username)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param username
     *
     * @return
     */
    public boolean followed(String username) {
        if (followers.contains(username)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public Boolean getEmailVerified() {
        return emailVerified;
    }

    /**
     *
     * @param emailVerified
     */
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     *
     * @return
     */
    public String getBase32secret() {
        return base32secret;
    }

    /**
     *
     * @return
     */
    public String getBio() {
        return bio;
    }

    /**
     *
     * @param bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     */
    public Set<String> getFollowers() {
        return followers;
    }

    /**
     *
     * @param followers
     */
    public void setFollowers(Set<String> followers) {
        this.followers = followers;
    }

    /**
     *
     * @return
     */
    public Set<String> getFollowing() {
        return following;
    }

    /**
     *
     * @param following
     */
    public void setFollowing(Set<String> following) {
        this.following = following;
    }

    /**
     *
     * @param username
     * @param image
     * @param type
     * @param length
     */
    public void setProfilePicture(String username, byte[] image, String type, int length) {
        try {
            FileOutputStream fos = null;
            String filetype = Convertors.SplitFiletype(type)[1];
            ByteBuffer imageBuffer = ByteBuffer.wrap(image);
            new File("/var/tmp/instagrim/").mkdirs();
            fos = new FileOutputStream(new File("/var/tmp/instagrim/" + username));
            fos.write(image);
            Session session = cluster.connect("instagrim");
            PreparedStatement psInsertProfilePic = session.prepare("UPDATE accounts SET profile_pic = ?, type = ?, length = ? WHERE username = ?");
            BoundStatement bsInsertProfilePic = new BoundStatement(psInsertProfilePic);

            session.execute(bsInsertProfilePic.bind(imageBuffer, type, length, username));
            session.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public ByteBuffer getProfilePicture() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT profile_pic FROM accounts WHERE username = ?");

        BoundStatement bs = new BoundStatement(ps);
        ResultSet results = session.execute(bs.bind(this.username));
        ByteBuffer imageBuffer = null;
        String type = null;
        int length = 0;
        if (results.isExhausted()) {
            return null;
        } else {
            for (Row row : results) {
                imageBuffer = row.getBytes("profile_pic");
            }
        }
        session.close();
        return imageBuffer;
    }
}
