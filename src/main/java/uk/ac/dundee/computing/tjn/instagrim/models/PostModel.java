package uk.ac.dundee.computing.tjn.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import uk.ac.dundee.computing.tjn.instagrim.lib.CassandraHosts;

public class PostModel {

    private UUID postID;
    private String username;
    private LocalDate posted;
    private String caption;
    private Set<String> likes;
    private Set<UUID> comments;

    private Cluster cluster;

    public PostModel() {
        cluster = CassandraHosts.getCluster();
    }

    public PostModel(UUID postID, String username, String caption) {
        this.postID = postID;
        this.username = username;
        this.caption = caption;
    }

    public void Pull() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("SELECT * FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        bs.bind(this.postID);
        ResultSet rs = session.execute(bs);

        for (Row row : rs) {
            this.postID = row.getUUID("postid");
            this.username = row.getString("user");
            this.posted = row.getDate("posted");
            this.caption = row.getString("caption");
            this.likes = row.getSet("likes", String.class);
            this.comments = row.getSet("comments", UUID.class);
        }
    }

    public void Post() {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("INSERT INTO posts (postid, user, posted, caption, likes, comments) Values(?,?,?,?,?,?)");
        BoundStatement bs = new BoundStatement(ps);
        this.posted = LocalDate.fromMillisSinceEpoch(System.currentTimeMillis());
        this.likes = new TreeSet<>();
        this.comments = new TreeSet<>();
        session.execute(bs.bind(this.postID, this.username, this.posted, this.caption, this.likes, this.comments));
    }

    public UUID getPostID() {
        return postID;
    }

    public void setPostID(UUID postID) {
        this.postID = postID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
