package uk.ac.dundee.computing.tjn.instagrim.stores;

import java.util.Date;
import java.util.UUID;

/**
 *
 * @author James Neill
 */
public class CommentStore {

    private UUID commentID;
    private String username;
    private Date posted;
    private String caption;

    /**
     * Gets the comment's commentID
     *
     * @return the comment ID
     */
    public UUID getCommentID() {
        return commentID;
    }

    /**
     * Sets the comment's commentID
     *
     * @param commentID the comment ID
     */
    public void setCommentID(UUID commentID) {
        this.commentID = commentID;
    }

    /**
     * Gets the username of the person that made the comment
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the person that made the comment
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the date of when the comment was posted
     *
     * @return
     */
    public Date getPosted() {
        return posted;
    }

    /**
     * Sets the date of when the comment was posted
     *
     * @param posted
     */
    public void setPosted(Date posted) {
        this.posted = posted;
    }

    /**
     * Gets the caption of the comment
     *
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption of the comment
     *
     * @param caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
}
