package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.models.ImageModel;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

@WebServlet(name = "Post", urlPatterns = {"/post", "/post/edit/*"})
public class Post extends HttpServlet {

    private Cluster cluster = CassandraHosts.getCluster();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/post.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            String type = part.getContentType();
            String filename = part.getSubmittedFileName();

            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            HttpSession session = request.getSession();
            SessionStore logInHandler = (SessionStore) session.getAttribute("LoggedIn");
            String username = "majed";
            if (logInHandler.isLoggedIn()) {
                username = logInHandler.getUsername();
            }
            if (i > 0) {
                byte[] b = new byte[i + 1];
                is.read(b);
                System.out.println("Length : " + b.length);
                ImageModel tm = new ImageModel(cluster);
                tm.insertImage(b, type, filename, username);

                is.close();
            }
            RequestDispatcher rd = request.getRequestDispatcher("/upload.jsp");
            rd.forward(request, response);
        }
    }
}
