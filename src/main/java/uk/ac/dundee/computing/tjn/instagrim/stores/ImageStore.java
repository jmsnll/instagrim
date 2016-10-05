package uk.ac.dundee.computing.tjn.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ImageStore {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private UUID id = null;

    public void Image() {

    }

    public void setID(UUID id) {
        this.id = id;
    }

    public String getID() {
        return id.toString();
    }

    public void setImage(ByteBuffer bImage, int length, String type) {
        this.bImage = bImage;
        this.length = length;
        this.type = type;
    }

    public ByteBuffer getBuffer() {
        return bImage;
    }

    public int getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public byte[] getBytes() {
        byte image[] = Bytes.getArray(bImage);
        return image;
    }
}
