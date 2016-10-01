package uk.ac.dundee.computing.tjn.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;

/**
 *
 * @author thms
 */
public class ImageStore {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private java.util.UUID UUID = null;

    /**
     *
     */
    public void Image() {

    }

    /**
     *
     * @param UUID
     */
    public void setUUID(java.util.UUID UUID) {
        this.UUID = UUID;
    }

    /**
     *
     * @return
     */
    public String getSUUID() {
        return UUID.toString();
    }

    /**
     *
     * @param bImage
     * @param length
     * @param type
     */
    public void setImage(ByteBuffer bImage, int length, String type) {
        this.bImage = bImage;
        this.length = length;
        this.type = type;
    }

    /**
     *
     * @return
     */
    public ByteBuffer getBuffer() {
        return bImage;
    }

    /**
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @return
     */
    public byte[] getBytes() {
        byte image[] = Bytes.getArray(bImage);
        return image;
    }

}
