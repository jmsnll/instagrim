package uk.ac.dundee.computing.tjn.instagrim.models;

import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.tjn.instagrim.stores.PostStore;

public class PostModel {

    Cluster cluster = CassandraHosts.getCluster();

    private String username;
    private UUID postID;
    private LocalDate posted;
    private String caption;
    private Set<String> likes;
    private Set<UUID> comments;

    public PostModel() {
    }

    public PostModel(UUID postID) {
        this.postID = postID;
        pull();
    }

    private void pull() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT * FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        bs.bind(this.postID);
        ResultSet rs = session.execute(bs);

        for (Row row : rs) {
            this.postID = row.getUUID("postid");
            this.username = row.getString("username");
            //this.posted = row.getDate("posted");
            this.caption = row.getString("caption");
            this.likes = row.getSet("likes", String.class);
            this.comments = row.getSet("comments", UUID.class);
        }
    }

    public void createPost(String username, String caption, byte[] image, String type) {
        FileOutputStream fos = null;
        try {
            String filetype = Convertors.SplitFiletype(type)[1];
            ByteBuffer imageBuffer = ByteBuffer.wrap(image);
            UUID postID = Convertors.getTimeUUID();
            new File("/var/tmp/instagrim/").mkdirs();
            fos = new FileOutputStream(new File("/var/tmp/instagrim/" + postID));
            fos.write(image);
            byte[] thumbnail = imageResize(postID.toString(), filetype);
            ByteBuffer thumbnailBuffer = ByteBuffer.wrap(thumbnail);
            byte[] processed = imageDecolour(postID.toString(), filetype);
            ByteBuffer processedBuffer = ByteBuffer.wrap(processed);
            Session session = cluster.connect("instagrim");
            PreparedStatement psInsertPost = session.prepare("INSERT INTO posts (postid, username, posted, caption, likes, comments, image, thumbnail, processed, imageLength, thumbnailLength, processedLength, type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertAccountPosts = session.prepare("INSERT INTO accountposts (postid, username, posted) VALUES (?,?,?)");
            BoundStatement bsInsertPost = new BoundStatement(psInsertPost);
            BoundStatement bsInsertAccountPosts = new BoundStatement(psInsertAccountPosts);

            Date now = new Date(System.currentTimeMillis());

            TreeSet emptyTreeSet = new TreeSet();
            session.execute(bsInsertPost.bind(postID, username, now, caption, emptyTreeSet, emptyTreeSet, imageBuffer, thumbnailBuffer, processedBuffer, image.length, thumbnail.length, processed.length, type));
            session.execute(bsInsertAccountPosts.bind(postID, username, now));
            session.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PostModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PostModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(PostModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean exists(UUID post) {
        Session session = CassandraHosts.getCluster().connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT postid FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet rs = session.execute(bs.bind(post));
        if (rs.isExhausted()) {
            return false;
        }
        return true;
    }

    public byte[] imageResize(String postID, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + postID));
            BufferedImage thumbnail = createThumbnail(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {

        }
        return null;
    }

    public byte[] imageDecolour(String postID, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + postID));
            BufferedImage processed = createProcessed(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {

        }
        return null;
    }

    public static BufferedImage createThumbnail(BufferedImage image) {
        image = resize(image, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(image, 2);
    }

    public static BufferedImage createProcessed(BufferedImage image) {
        int Width = image.getWidth() - 1;
        image = resize(image, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(image, 4);
    }

    public PostStore getPost(UUID postID) {
        Session session = CassandraHosts.getCluster().connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT * FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet results = session.execute(bs.bind(postID));
        if (!results.isExhausted()) {
            for (Row row : results) {
                PostStore post = new PostStore();
                post.setPostID(postID);
                post.setCaption(row.getString("caption"));
                post.setComments(row.getSet("comments", UUID.class));
                post.setLikes(row.getSet("likes", String.class));
                //post.setPosted(row.getDate("posted"));
                post.setUsername(row.getString("username"));
                post.setImage(row.getBytes("image"));
                post.setType(row.getString("type"));
                post.setLength(row.getInt("imagelength"));
                return post;
            }
        }
        return null;
    }

    public LinkedList<PostStore> getUsersPosts(String username) {
        LinkedList<PostStore> posts = new LinkedList<>();
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT postid FROM posts WHERE username = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet results = session.execute(bs.bind(username));
        if (results.isExhausted()) {
            return null;
        } else {
            for (Row row : results) {
                PostStore post = new PostStore();
                UUID id = row.getUUID("postid");
                post.setPostID(id);
                post.setCaption(row.getString("caption"));
                post.setComments(row.getSet("comments", UUID.class));
                post.setLikes(row.getSet("likes", String.class));
//                post.setPosted(row.getTimestamp("posted"));
                post.setUsername(row.getString("username"));
                posts.add(post);
            }
        }
        return posts;
    }

    public LinkedList<PostStore> getMostRecentPosts() {
        return getMostRecentPosts(10);
    }

    public LinkedList<PostStore> getMostRecentPosts(int count) {
        LinkedList<PostStore> posts = new LinkedList<>();
        Session session = cluster.connect("instagrim");
        SimpleStatement statement = new SimpleStatement("SELECT * FROM posts LIMIT " + count);
        ResultSet results = session.execute(statement);

        if (results.isExhausted()) {

        } else {
            for (Row row : results) {
                PostStore post = new PostStore();
                UUID id = row.getUUID("postid");
                post.setPostID(id);
                post.setCaption(row.getString("caption"));
                post.setComments(row.getSet("comments", UUID.class));
                post.setLikes(row.getSet("likes", String.class));
//              post.setPosted(row.getTimestamp("posted"));
                post.setUsername(row.getString("username"));
                posts.add(post);
            }
        }
        return posts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getPostID() {
        return postID;
    }

    public void setPostID(UUID postID) {
        this.postID = postID;
    }

    public LocalDate getPosted() {
        return posted;
    }

    public void setPosted(LocalDate posted) {
        this.posted = posted;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Set<String> getLikes() {
        return likes;
    }

    public void setLikes(Set<String> likes) {
        this.likes = likes;
    }

    public Set<UUID> getComments() {
        return comments;
    }

    public void setComments(Set<UUID> comments) {
        this.comments = comments;
    }
}
