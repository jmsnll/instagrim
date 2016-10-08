package uk.ac.dundee.computing.tjn.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.dundee.computing.tjn.instagrim.lib.PasswordStorage;
import uk.ac.dundee.computing.tjn.instagrim.lib.TwoFactorAuthUtil;

public class UserModel {

    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private String email;
    private Boolean emailVerified;
    private String base32secret;
    private String bio;

    private final Cluster cluster;

    private TwoFactorAuthUtil twoFactorHandler = new TwoFactorAuthUtil();

    public UserModel(String username, Cluster cluster) {
        this.username = username;
        this.cluster = cluster;
        pull();
    }

    public UserModel(String username, String password, String email, String first_name, String last_name, Cluster cluster) {
        this.username = username;
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.base32secret = null;
        this.cluster = cluster;
    }

    public void pull() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT * FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        bs.bind(this.username);
        ResultSet rs = session.execute(bs);

        for (Row row : rs) {
            this.password = row.getString("password");
            this.first_name = row.getString("first_name");
            this.last_name = row.getString("last_name");
            this.email = row.getString("email");
            this.emailVerified = row.getBool("emailVerified");
            this.base32secret = row.getString("base32secret");
            this.bio = row.getString("bio");
        }
    }

    public void push() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("UPDATE instagrim.accounts SET password = ?, first_name = ?, last_name = ?, email = ?, emailVerified = ?, base32secret = ?, bio = ? WHERE username = ?;");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(this.password, this.first_name, this.last_name, this.email, this.emailVerified, this.base32secret, this.bio, this.username));
    }

    public boolean isTwoFactorEnabled() {
        if (this.base32secret == null) {
            return false;
        }
        return true;
    }

    public void enableTwoFactor() {
        this.base32secret = twoFactorHandler.generateBase32Secret();
    }

    public void disableTwoFactor() {
        this.base32secret = null;
    }

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

    public boolean Register() {
        if (Exists(username, cluster)) {
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("INSERT INTO accounts (username, password, first_name, last_name, email, emailVerified) Values(?,?,?,?,?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(username, password, first_name, last_name, email, false));
        return true;
    }

    public boolean Delete() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("DELETE FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(this.username));
        return true;
    }

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

    public static boolean Exists(String username, Cluster cluster) {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT username FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet rs = session.execute(bs.bind(username));
        if (rs.isExhausted()) {
            return false;
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getBase32secret() {
        return base32secret;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}