package uk.ac.dundee.computing.aec.instagrim.models;

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
import uk.ac.dundee.computing.aec.instagrim.lib.AeSimpleSHA1;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.lib.TwoFactorAuthUtil;

public class User {

    private String name;
    private String username;
    private String passwordHash;
    private String email;
    private Boolean emailVerified;
    private String base32secret;
    
    private Boolean active;

    private Cluster cluster;

    TwoFactorAuthUtil twoFactor = new TwoFactorAuthUtil();

    public User(String username, String password, Cluster cluster) {
        this.username = username;
        try {
            this.passwordHash = AeSimpleSHA1.SHA1(password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.active = true;
        this.cluster = cluster;
    }

    public User(String username, String password, String email, String name) {
        this.username = username;
        try {
            this.passwordHash = AeSimpleSHA1.SHA1(password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.email = email;
        this.name = name;
        this.base32secret = twoFactor.generateBase32Secret();
        this.active = true;
        this.cluster = cluster;
    }

    public String getCurrentTwoFactorCode() {
        String code = null;
        try {
            code = twoFactor.generateCurrentNumber(this.base32secret);
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return code;
    }
    
    public Boolean isValidCode(String code) {
        try {
            if(code.equals(twoFactor.generateCurrentNumber(this.base32secret))) {
                return true;
            }
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean Register() {
        return true;
    }

    public boolean Deactivate() {
        this.active = false;
        return true;
    }
    
    public boolean Reactivate() {
        this.active = true;
        return true;
    }

    public boolean Delete() {
        Session session = cluster.connect("instagrim");
        
        return true;
    }

    public boolean RegisterUser(String username, String Password) {
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String EncodedPassword = null;
        try {
            EncodedPassword = sha1handler.SHA1(Password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password) Values(?,?)");

        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username, EncodedPassword));
        //We are assuming this always works.  Also a transaction would be good here !

        return true;
    }

    public boolean IsValidUser(String username, String Password) {
        AeSimpleSHA1 sha1handler = new AeSimpleSHA1();
        String EncodedPassword = null;
        try {
            EncodedPassword = sha1handler.SHA1(Password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {

                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(EncodedPassword) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public String getEmailAddress() {
        return this.email;
    }

    public Boolean isEmailVerified() {
        return this.emailVerified;
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }
}
