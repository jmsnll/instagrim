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

/**
 *
 * @author James Neill
 */
public class PostModel {

    Cluster cluster = CassandraHosts.getCluster();

    private String username;
    private UUID postID;
    private LocalDate posted;
    private String caption;
    private Set<String> likes;
    private Set<UUID> comments;

    /**
     *
     */
    public PostModel() {
    }

    /**
     *
     * @param postID
     */
    public PostModel(UUID postID) {
        // set the id of the post
        this.postID = postID;
        // load the information from the database
        pull();
    }

    private void pull() {
        // connect to the cluster
        Session session = cluster.connect("instagrim");
        // select all information where the postid equals this.postID
        PreparedStatement ps = session.prepare("SELECT * FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        bs.bind(this.postID);
        ResultSet results = session.execute(bs);
        // foreach row in the results
        for (Row row : results) {
            // get the postid, username, caption, likes and comments
            this.postID = row.getUUID("postid");
            this.username = row.getString("username");
            //this.posted = row.getDate("posted");
            this.caption = row.getString("caption");
            this.likes = row.getSet("likes", String.class);
            this.comments = row.getSet("comments", UUID.class);
        }
    }

    /**
     *
     * @param username
     * @param caption
     * @param image
     * @param type
     */
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
            PreparedStatement psInsertPostCaption = session.prepare("INSERT INTO postcomments (commentid, caption) VALUES (?,?)");

            BoundStatement bsInsertPost = new BoundStatement(psInsertPost);
            BoundStatement bsInsertAccountPosts = new BoundStatement(psInsertAccountPosts);
            BoundStatement bsInsertPostCaption = new BoundStatement(psInsertPostCaption);

            Date now = new Date(System.currentTimeMillis());

            TreeSet emptyTreeSet = new TreeSet();
            session.execute(bsInsertPost.bind(postID, username, now, caption, emptyTreeSet, emptyTreeSet, imageBuffer, thumbnailBuffer, processedBuffer, image.length, thumbnail.length, processed.length, type));
            session.execute(bsInsertAccountPosts.bind(postID, username, now));
            session.execute(bsInsertPostCaption.bind(postID, caption));
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

    /**
     *
     * @param post
     *
     * @return
     */
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

    /**
     *
     * @param postID
     * @param type
     *
     * @return
     */
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

    /**
     *
     * @param postID
     * @param type
     *
     * @return
     */
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

    /**
     *
     * @param image
     *
     * @return
     */
    public static BufferedImage createThumbnail(BufferedImage image) {
        image = resize(image, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(image, 2);
    }

    /**
     *
     * @param image
     *
     * @return
     */
    public static BufferedImage createProcessed(BufferedImage image) {
        int Width = image.getWidth() - 1;
        image = resize(image, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(image, 4);
    }

    /**
     *
     * @param postID
     *
     * @return
     */
    public PostStore getPost(UUID postID) {
        // connect to the cluster
        Session session = CassandraHosts.getCluster().connect("instagrim");
        // get the specified post
        PreparedStatement ps = session.prepare("SELECT * FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet results = session.execute(bs.bind(postID));
        // if there are results
        if (!results.isExhausted()) {
            // for each result
            for (Row row : results) {
                // create a new post and set its properties
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
                // return the post
                return post;
            }
        }
        //otherwise return null
        return null;
    }

    /**
     *
     * @param query
     *
     * @return
     */
    public static LinkedList<PostStore> searchPosts(String query) {
        // create a linked list to store all the posts
        LinkedList<PostStore> posts = new LinkedList<>();
        // connect to the cluster
        Session session = CassandraHosts.getCluster().connect("instagrim");
        // select all columns from the postcomments table where the caption contains the query
        PreparedStatement ps = session.prepare("SELECT * FROM postcomments WHERE caption CONTAINS ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet results = session.execute(bs.bind(query));
        if (results.isExhausted()) {
            return null;
        } else {
            for (Row row : results) {
                PostStore post = new PostStore();
                post.setPostID(row.getUUID("postid"));
            }
        }
        return posts;
    }

    /**
     *
     * @param username
     *
     * @return
     */
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

    /**
     *
     * @return
     */
    public LinkedList<PostStore> getMostRecentPosts() {
        return getMostRecentPosts(10);
    }

    /**
     *
     * @param count
     *
     * @return
     */
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

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public UUID getPostID() {
        return postID;
    }

    /**
     *
     * @param postID
     */
    public void setPostID(UUID postID) {
        this.postID = postID;
    }

    /**
     *
     * @return
     */
    public LocalDate getPosted() {
        return posted;
    }

    /**
     *
     * @param posted
     */
    public void setPosted(LocalDate posted) {
        this.posted = posted;
    }

    /**
     *
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     *
     * @param caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     *
     * @return
     */
    public Set<String> getLikes() {
        return likes;
    }

    /**
     *
     * @param likes
     */
    public void setLikes(Set<String> likes) {
        this.likes = likes;
    }

    /**
     *
     * @return
     */
    public Set<UUID> getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     */
    public void setComments(Set<UUID> comments) {
        this.comments = comments;
    }
}
