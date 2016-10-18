package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.models.PostModel;
import uk.ac.dundee.computing.tjn.instagrim.models.UserModel;
import uk.ac.dundee.computing.tjn.instagrim.stores.ProfileStore;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

@WebServlet(urlPatterns = {"/profile", "/profile/*"})
@MultipartConfig

public class Profile extends HttpServlet {

    private Cluster cluster;

    public Profile() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        switch (args.length) {
            // instagrim/profile
            case 2:
                DisplayCurrentProfile(request, response);
                break;
            // instagrim/profile/<username>
            case 3:
                DisplayUser(args[2], request, response);
                break;
            // instagrim/profile/<username>/<postid>
            case 4:
                DisplayPost(UUID.fromString(args[3]), request, response);
                break;
        }
    }

    private void DisplayPost(UUID postID, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewpost.jsp");
        HttpSession session = request.getSession();
        if (PostModel.exists(postID, cluster)) {
            PostModel post = new PostModel(postID, cluster);
            session.setAttribute("post", post);
            rd.forward(request, response);
        } else {
            DisplayError(request, response);
        }
    }

    private void DisplayUser(String username, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewprofile.jsp");
        HttpSession session = request.getSession();
        if (UserModel.exists(username, cluster)) {
            UserModel user = new UserModel(username, cluster);
            ProfileStore profile = new ProfileStore(user);

            session.setAttribute("Profile", profile);
            rd.forward(request, response);
        } else {
            DisplayError(request, response);
        }
    }

    private void DisplayCurrentProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewprofile.jsp");
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        if (sessionStore != null) {
            DisplayUser(sessionStore.getUsername(), request, response);
        } else {
            response.sendRedirect("/Instagrim/404.html");
        }
    }

    private void DisplayError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewprofile.jsp");
        HttpSession session = request.getSession();
        session.setAttribute("UserNotFound", true);
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
