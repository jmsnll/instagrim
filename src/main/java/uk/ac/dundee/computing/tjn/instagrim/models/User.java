package uk.ac.dundee.computing.tjn.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.dundee.computing.tjn.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.tjn.instagrim.lib.TwoFactorAuthUtil;
import org.krysalis.barcode4j.*;

public class User {

    private String name;
    private String username;
    private String passwordSHA1;
    private String email;
    private Boolean emailVerificationStatus;
    private String base32secret;

    private Cluster cluster;

    TwoFactorAuthUtil twoFactorHandler = new TwoFactorAuthUtil();

    public User(String username, String password, Cluster cluster) {
        this.username = username;
        try {
            this.passwordSHA1 = AeSimpleSHA1.SHA1(password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.cluster = cluster;
    }

    public User(String username, String password, String email, String name, Cluster cluster) {
        this.username = username;
        try {
            this.passwordSHA1 = AeSimpleSHA1.SHA1(password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.email = email;
        this.name = name;
        this.base32secret = null;
        this.cluster = cluster;
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
        PreparedStatement ps = session.prepare("insert into accounts (name, username, passwordSHA1, email) Values(?,?,?,?)");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(name, username, passwordSHA1, email));
        return true;
    }

    public boolean Delete() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("DELETE FROM accounts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(this.username));
                
        return true;
    }

    public boolean isValidUser(String username, String Password) {
        String EncodedPassword = null;
        try {
            EncodedPassword = AeSimpleSHA1.SHA1(Password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select passwordSHA1 from accounts where username = ?");
        BoundStatement boundStatement = new BoundStatement(ps);
        ResultSet rs = session.execute(boundStatement.bind(username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {
                String StoredPass = row.getString("passwordSHA1");
                if (StoredPass.compareTo(EncodedPassword) == 0) {
                    return true;
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

    public String getPasswordSHA1() {
        return passwordSHA1;
    }

    public void setPasswordSHA1(String passwordSHA1) {
        this.passwordSHA1 = passwordSHA1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerificationStatus() {
        return emailVerificationStatus;
    }

    public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
        this.emailVerificationStatus = emailVerificationStatus;
    }

}
