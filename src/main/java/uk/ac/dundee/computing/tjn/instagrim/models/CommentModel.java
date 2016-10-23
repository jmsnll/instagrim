package uk.ac.dundee.computing.tjn.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import java.util.Iterator;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.stores.CommentStore;

public class CommentModel {

    Cluster cluster;

    public CommentModel(Cluster cluster) {
        this.cluster = cluster;
    }

    public void addComment(UUID picID, String username, String comment) {
        Session session = cluster.connect("instagrim");
        UUID commentID = Convertors.getTimeUUID();

        //PreparedStatement psInsertInComment = session.prepare("INSERT INTO postcomments (commentid, username, posted, caption) values (?,?,?,?)");
        PreparedStatement psInsertInComment = session.prepare("INSERT INTO postcomments (commentid, username, caption) values (?,?,?)");
        PreparedStatement psInsertInPost = session.prepare("UPDATE posts SET comments = comments +{" + commentID + "} where postid = ?");
        BoundStatement bsInsertInComment = new BoundStatement(psInsertInComment);
        BoundStatement bsInsertInPost = new BoundStatement(psInsertInPost);

        Date now = new Date();
        session.execute(bsInsertInComment.bind(commentID, username, comment));
        session.execute(bsInsertInPost.bind(picID));
    }

    public void removeComment(UUID commentID) {
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("DELETE FROM postcomments WHERE commentid = ?");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(commentID));
    }

    public LinkedList<UUID> getCommentIDs(UUID postID) {
        Session session = cluster.connect("instagrim");
        LinkedList<UUID> comments = new LinkedList<>();

        PreparedStatement ps = session.prepare("SELECT comments FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet results = session.execute(bs.bind(postID));
        if (results.isExhausted()) {
            return null;
        } else {
            for (Row row : results) {
                Set<UUID> commentsSet = row.getSet("comments", UUID.class);
                for (UUID id : commentsSet) {
                    comments.add(id);
                }
            }
        }
        return comments;
    }

    public LinkedList<CommentStore> getComments(UUID postID) {
        return getComments(getCommentIDs(postID));
    }

    public LinkedList<CommentStore> getComments(LinkedList<UUID> commentIDs) {
        Session session = cluster.connect("instagrim");
        LinkedList<CommentStore> comments = new LinkedList<>();

        PreparedStatement ps = session.prepare("SELECT username, posted, caption FROM postcomments WHERE commentid = ?");
        BoundStatement bs = new BoundStatement(ps);
        for (UUID id : commentIDs) {
            ResultSet results = session.execute(bs.bind(id));
            if (results.isExhausted()) {
                return null;
            } else {
                for (Row row : results) {
                    CommentStore comment = new CommentStore();
                    String username = row.getString("username");
                    Date posted = row.getTimestamp("posted");
                    String caption = row.getString("caption");
                    comment.setCommentID(id);
                    comment.setUsername(username);
                    comment.setCaption(caption);
                    comment.setPosted(posted);
                    comments.add(comment);
                }
            }
        }
        return comments;
    }
}
