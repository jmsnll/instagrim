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

public class User {

    private String username;
    private String password;
    private String name;
    private String email;
    private Boolean emailVerified;
    private String base32secret;
    
    private String biography;

    private final Cluster cluster;

    private TwoFactorAuthUtil twoFactorHandler = new TwoFactorAuthUtil();

    public User(String username, Cluster cluster) {
        this.username = username;
        this.cluster = cluster;
        loadUserDetails();
    }

    public User(String username, String password, Cluster cluster) {
        this.username = username;
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.cluster = cluster;
    }

    public User(String username, String password, String email, String name, Cluster cluster) {
        this.username = username;
        try {
            this.password = PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.email = email;
        this.name = name;
        this.base32secret = null;
        this.cluster = cluster;
    }

    private void loadUserDetails() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT * FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        bs.bind(this.username);
        ResultSet rs = session.execute(bs);

        for (Row row : rs) {
            this.password = row.getString("password");
            this.name = row.getString("name");
            this.email = row.getString("email");
            this.emailVerified = row.getBool("emailVerified");
            this.base32secret = row.getString("base32secret");
            this.biography = row.getString("biography");
        }
    }

    public boolean isTwoFactorEnabled() {
        if (this.base32secret == null) {
            return false;
        }
        return true;
    }

    public final void enableTwoFactor() {
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
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return code;
    }

    public Boolean isValidTwoFactorCode(String code) {
        try {
            if (code.equals(twoFactorHandler.generateCurrentNumber(this.base32secret))) {
                return true;
            }
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean Register() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("INSERT INTO accounts (name, username, password, email) Values(?,?,?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(name, username, password, email));
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
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute(boundStatement.bind(username));
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
                    Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

}
