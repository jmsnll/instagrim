package uk.ac.dundee.computing.tjn.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import uk.ac.dundee.computing.tjn.instagrim.stores.CommentStore;

/**
 *
 * @author James Neill
 */
public class CommentModel {

    Cluster cluster;

    /**
     *
     * @param cluster
     */
    public CommentModel(Cluster cluster) {
        this.cluster = cluster;
    }

    /**
     *
     * @param picID
     * @param username
     * @param comment
     */
    public void addComment(UUID picID, String username, String comment) {
        // Connects to the cassandra cluster and generate a new UUID for the comment
        Session session = cluster.connect("instagrim");
        UUID commentID = Convertors.getTimeUUID();

        //PreparedStatement psInsertInComment = session.prepare("INSERT INTO postcomments (commentid, username, posted, caption) values (?,?,?,?)");
        // insert the comment into postcomments & then add the posts ID to the comments set in posts
        PreparedStatement psInsertInComment = session.prepare("INSERT INTO postcomments (commentid, username, caption) values (?,?,?)");
        PreparedStatement psInsertInPost = session.prepare("UPDATE posts SET comments = comments +{" + commentID + "} where postid = ?");
        BoundStatement bsInsertInComment = new BoundStatement(psInsertInComment);
        BoundStatement bsInsertInPost = new BoundStatement(psInsertInPost);

        // execute the statements
        session.execute(bsInsertInComment.bind(commentID, username, comment));
        session.execute(bsInsertInPost.bind(picID));
    }

    /**
     *
     * @param commentID
     */
    public void removeComment(UUID commentID) {
        // connect to the cassandra cluster
        Session session = cluster.connect("instagrim");
        // delete the comment from the postcomments table
        PreparedStatement ps = session.prepare("DELETE FROM postcomments WHERE commentid = ?");
        BoundStatement bs = new BoundStatement(ps);
        session.execute(bs.bind(commentID));
    }

    /**
     *
     * @param postID
     *
     * @return
     */
    public LinkedList<CommentStore> getComments(UUID postID) {
        return getComments(getCommentIDs(postID));
    }

    /**
     *
     * @param commentIDs
     *
     * @return
     */
    public LinkedList<CommentStore> getComments(LinkedList<UUID> commentIDs) {
        // Connect to the cassandra cluster
        Session session = cluster.connect("instagrim");
        // LinkedList to store all the comments
        LinkedList<CommentStore> comments = new LinkedList<>();

        // Select the username, time of posting and the caption from the postcomments table where the id matches the one specified
        PreparedStatement ps = session.prepare("SELECT username, posted, caption FROM postcomments WHERE commentid = ?");
        BoundStatement bs = new BoundStatement(ps);
        // for each UUID in the LinkedList
        for (UUID id : commentIDs) {
            // run the boundstatement
            ResultSet results = session.execute(bs.bind(id));
            // if there are no results
            if (results.isExhausted()) {
                // return null
                return null;
            } else {
                // otherwise for each row in the results
                for (Row row : results) {
                    // create a new comment store
                    CommentStore comment = new CommentStore();
                    // get the username, time of posting and the caption of the comment
                    String username = row.getString("username");
                    Date posted = row.getTimestamp("posted");
                    String caption = row.getString("caption");
                    // and set them into the comment store
                    comment.setCommentID(id);
                    comment.setUsername(username);
                    comment.setCaption(caption);
                    comment.setPosted(posted);
                    // add the comment to the list of comments
                    comments.add(comment);
                }
            }
        }
        // return list of comments
        return comments;
    }

    private LinkedList<UUID> getCommentIDs(UUID postID) {
        // connect to the instagrim cluster
        Session session = cluster.connect("instagrim");
        LinkedList<UUID> comments = new LinkedList<>();

        // Select the comments set from the posts table where the postid matches that specified
        PreparedStatement ps = session.prepare("SELECT comments FROM posts WHERE postid = ?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet results = session.execute(bs.bind(postID));
        // if there are no results
        if (results.isExhausted()) {
            // return null
            return null;
        } else {
            // otherwise for each row in the results (should be only one result)
            for (Row row : results) {
                // get the comments associated with the post
                Set<UUID> commentsSet = row.getSet("comments", UUID.class);
                // for each UUID in the set of comments
                for (UUID id : commentsSet) {
                    // add it to the linked list
                    comments.add(id);
                }
            }
        }
        // return the list of comment UUID's
        return comments;
    }
}
