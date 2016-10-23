package uk.ac.dundee.computing.tjn.instagrim.stores;

import java.util.Date;
import java.util.UUID;

public class CommentStore {

    private UUID commentID;
    private String username;
    private Date posted;
    private String caption;

    public UUID getCommentID() {
        return commentID;
    }

    public void setCommentID(UUID commentID) {
        this.commentID = commentID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getPosted() {
        return posted;
    }

    public void setPosted(Date posted) {
        this.posted = posted;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
