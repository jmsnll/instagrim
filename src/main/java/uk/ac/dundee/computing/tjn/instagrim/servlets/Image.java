package uk.ac.dundee.computing.tjn.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
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
import uk.ac.dundee.computing.tjn.instagrim.stores.PostStore;
import uk.ac.dundee.computing.tjn.instagrim.stores.SessionStore;

@WebServlet(urlPatterns = {
    "/image",
    "/image/*",
    "/thumb/*",
    "/avatar/*"
})
@MultipartConfig

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private final HashMap CommandsMap = new HashMap();

    public Image() {
        super();
        CommandsMap.put("image", 1);
        CommandsMap.put("thumb", 2);
        CommandsMap.put("avatar", 3);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.cluster = CassandraHosts.getCluster();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception ex) {
            error("Bad Operator", response);
            return;
        }
        switch (command) {
            case 1:
                displayImage(args[2], response);
                break;
            case 2:
                displayImage(args[2], response);
                break;
//            case 3:
//                displayProfileImage(args[2], response);
            default:
                error("Bad Operator", response);
        }
    }

    private void displayImage(String postID, HttpServletResponse response) throws ServletException, IOException {
        PostModel pm = new PostModel();
        PostStore post = pm.getPost(UUID.fromString(postID));
        OutputStream os = response.getOutputStream();

        response.setContentType(post.getType());
        response.setContentLength(post.getLength());
        InputStream is = new ByteArrayInputStream(post.getBytes());
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = bis.read(buffer)) > 0;) {
            os.write(buffer, 0, length);
        }
        os.close();
    }

//    private void displayProfileImage(String username, HttpServletResponse response) throws ServletException, IOException {
//        UserModel user = new UserModel(username, cluster);
//        ByteBuffer image = user.getProfilePicture();
//
//        OutputStream os = response.getOutputStream();
//
//        response.setContentType(image.getType());
//        response.setContentLength(image.getLength());
//
//        InputStream is = new ByteArrayInputStream(image.getBytes());
//        BufferedInputStream bis = new BufferedInputStream(is);
//        byte[] buffer = new byte[8192];
//        for (int length = 0; (length = bis.read(buffer)) > 0;) {
//            os.write(buffer, 0, length);
//        }
//        os.close();
//    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            String type = part.getContentType();
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
                PostModel post = new PostModel();
                String caption = request.getParameter("caption");
                post.createPost(username, caption, b, type);

                is.close();
            }
            RequestDispatcher rd = request.getRequestDispatcher("/post.jsp");
            rd.forward(request, response);
        }
    }

    private void error(String message, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter out = new PrintWriter(response.getOutputStream())) {
            out.println("<h1>You have an error in your input</h1>");
            out.println("<h2>" + message + "</h2>");
        }
    }
}
