package uk.ac.dundee.computing.tjn.instagrim.stores;

import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author James Neill
 */
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

    /**
     * Gets the posts image
     *
     * @return the image as a ByteBuffer
     */
    public ByteBuffer getImage() {
        return image;
    }

    /**
     * Sets the posts image
     *
     * @param image
     */
    public void setImage(ByteBuffer image) {
        this.image = image;
    }

    /**
     * Gets the type of the posts image
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the posts image
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the posts ID
     *
     * @return
     */
    public UUID getPostID() {
        return postID;
    }

    /**
     * Sets the posts ID
     *
     * @param postID
     */
    public void setPostID(UUID postID) {
        this.postID = postID;
    }

    /**
     * Gets the username of the user that made the post
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user that made the post
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the date when the post was posted
     *
     * @return
     */
    public LocalDate getPosted() {
        return posted;
    }

    /**
     * Sets the date when the post was posted
     *
     * @param posted
     */
    public void setPosted(LocalDate posted) {
        this.posted = posted;
    }

    /**
     * Gets the posts caption
     *
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the posts caption
     *
     * @param caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets the usernames of people who like the post
     *
     * @return
     */
    public Set<String> getLikes() {
        return likes;
    }

    /**
     * Sets the list of usernames of people who like the post
     *
     * @param likes
     */
    public void setLikes(Set<String> likes) {
        this.likes = likes;
    }

    /**
     * Gets the list of comments
     *
     * @return
     */
    public Set<UUID> getComments() {
        return comments;
    }

    /**
     * Sets the list of comments
     *
     * @param comments
     */
    public void setComments(Set<UUID> comments) {
        this.comments = comments;
    }

    /**
     * Gets the length of the image
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the length of the image
     *
     * @param length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Gets the image as a byte[]
     *
     * @return
     */
    public byte[] getBytes() {
        byte bytes[] = Bytes.getArray(image);
        return bytes;
    }
}
