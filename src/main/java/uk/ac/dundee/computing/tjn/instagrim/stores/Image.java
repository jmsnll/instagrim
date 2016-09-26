package uk.ac.dundee.computing.tjn.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;

public class Image {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private java.util.UUID UUID = null;

    public void Image() {

    }

    public void setUUID(java.util.UUID UUID) {
        this.UUID = UUID;
    }

    public String getSUUID() {
        return UUID.toString();
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