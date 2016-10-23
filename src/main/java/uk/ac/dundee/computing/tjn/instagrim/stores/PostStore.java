package uk.ac.dundee.computing.tjn.instagrim.stores;

import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class PostStore {

    private UUID postID;
    private String username;
    private LocalDate posted;
    private String caption;
    private Set<String> likes;
    private Set<UUID> comments;
    private ByteBuffer image = null;
    private int length;
    private String type;

    public ByteBuffer getImage() {
        return image;
    }

    public void setImage(ByteBuffer image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getBytes() {
        byte bytes[] = Bytes.getArray(image);
        return bytes;
    }
}
