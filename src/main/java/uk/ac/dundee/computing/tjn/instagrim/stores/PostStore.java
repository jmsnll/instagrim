package uk.ac.dundee.computing.tjn.instagrim.stores;

import com.datastax.driver.core.LocalDate;
import java.util.Set;
import java.util.UUID;

public class PostStore {

    private UUID imageID;
    private String username;
    private LocalDate posted;
    private String caption;
    private Set<String> likes;
    private Set<UUID> comments;

    public UUID getImageID() {
        return imageID;
    }

    public void setImageID(UUID imageID) {
        this.imageID = imageID;
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
