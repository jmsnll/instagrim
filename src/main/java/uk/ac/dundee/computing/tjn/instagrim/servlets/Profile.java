package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.http.Part;
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
                displayCurrentProfile(request, response);
                break;
            // instagrim/profile/<username>
            case 3:
                if (args[2].equals("create")) {
                    createProfile(request, response);
                } else {
                    displayUser(args[2], request, response);
                }
                break;
            // instagrim/profile/<username>/<postid>
            case 4:
                displayPost(UUID.fromString(args[3]), request, response);
                break;
        }
    }

    private void createProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/profile/createprofile.jsp");
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) request.getAttribute("LoggedIn");
        if (sessionStore.isLoggedIn()) {
            UserModel user = new UserModel(sessionStore.getUsername(), cluster);
            session.setAttribute("user", user);
        }
        rd.forward(request, response);
    }

    private void displayPost(UUID postID, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewpost.jsp");
        HttpSession session = request.getSession();
        if (PostModel.exists(postID, cluster)) {
            PostModel post = new PostModel(postID, cluster);
            session.setAttribute("post", post);
            rd.forward(request, response);
        } else {
            displayError(request, response);
        }
    }

    private void displayUser(String username, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewprofile.jsp");
        HttpSession session = request.getSession();
        if (UserModel.exists(username, cluster)) {
            UserModel user = new UserModel(username, cluster);
            ProfileStore profile = new ProfileStore(user);

            session.setAttribute("Profile", profile);
            rd.forward(request, response);
        } else {
            displayError(request, response);
        }
    }

    private void displayCurrentProfile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewprofile.jsp");
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        if (sessionStore != null) {
            displayUser(sessionStore.getUsername(), request, response);
        } else {
            response.sendRedirect("/Instagrim/404.html");
        }
    }

    private void displayError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/views/viewprofile.jsp");
        HttpSession session = request.getSession();
        session.setAttribute("UserNotFound", true);
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        SessionStore sessionStore = (SessionStore) session.getAttribute("LoggedIn");
        if (sessionStore == null || !sessionStore.isLoggedIn()) {
            response.sendRedirect("/Instagrim/login");
            return;
        }
        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());

            String type = part.getContentType();

            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            if (i > 0) {
                byte[] b = new byte[i + 1];
                is.read(b);
                System.out.println("Length : " + b.length);
                UserModel user = new UserModel(sessionStore.getUsername(), cluster);
                user.setProfilePicture(b);

                is.close();
            }
            RequestDispatcher rd = request.getRequestDispatcher("/views/viewprofile.jsp");
            rd.forward(request, response);
        }
    }
}
