package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.models.User;

@WebServlet(name = "Profile", urlPatterns = {"/profile/*"}, initParams = {
    @WebInitParam(name = "Name", value = "Value")})
public class Profile extends HttpServlet {

    private Cluster cluster;
    private User user;

    public Profile() {

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        String profileName = args[2];
        String postID = args[3];
        user = new User(profileName, cluster);
        try (PrintWriter pw = response.getWriter()) {
            pw.write("Profile: " + profileName);
            pw.write("\nPost ID: " + postID);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    private String getBase32Secret(String username) {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select * from accounts where username = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet rs = session.execute(bs.bind(username));
        if (rs.isExhausted()) {
            System.out.println("No user found with username: " + username);
            return null;
        }
        Row row = rs.one();
        return row.getString("base32secret");
    }
}
