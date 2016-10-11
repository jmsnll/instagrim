package uk.ac.dundee.computing.tjn.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as use keyspace2; CREATE TABLE
 * Tweets ( user varchar, interaction_time timeuuid, tweet varchar, PRIMARY KEY
 * (user,interaction_time) ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use: http://www.famkruithof.net/uuid/uuidgen
 */
import uk.ac.dundee.computing.tjn.instagrim.lib.Convertors;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.UUID;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.tjn.instagrim.stores.ImageStore;

public class ImageModel {

    Cluster cluster;

    public ImageModel(Cluster cluster) {
        this.cluster = cluster;
    }

    public void insertImage(byte[] b, String type, String name, String user) {
        try {

            String types[] = Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int length = b.length;
            UUID imageID = Convertors.getTimeUUID();

            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + imageID));

            output.write(b);
            byte[] thumbnail = imageResize(imageID.toString(), types[1]);
            int thumblength = thumbnail.length;
            ByteBuffer thumbbuf = ByteBuffer.wrap(thumbnail);
            byte[] processedb = imageDecolour(imageID.toString(), types[1]);
            ByteBuffer processedbuf = ByteBuffer.wrap(processedb);
            int processedlength = processedb.length;
            Session session = cluster.connect("instagrim");

            PreparedStatement psInsertImage = session.prepare("insert into images ( imageID, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertImageToUser = session.prepare("insert into posts ( imageID, user, image_added) values(?,?,?)");
            BoundStatement bsInsertImage = new BoundStatement(psInsertImage);
            BoundStatement bsInsertImageToUser = new BoundStatement(psInsertImageToUser);

            Date DateAdded = new Date();
            session.execute(bsInsertImage.bind(imageID, buffer, thumbbuf, processedbuf, user, DateAdded, length, thumblength, processedlength, type, name));
            session.execute(bsInsertImageToUser.bind(imageID, user, DateAdded));
            session.close();

        } catch (IOException ex) {
            System.out.println("Error --> " + ex);
        }
    }

    public byte[] imageResize(String imageID, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + imageID));
            BufferedImage thumbnail = createThumbnail(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {

        }
        return null;
    }

    public byte[] imageDecolour(String imageID, String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + imageID));
            BufferedImage processed = createProcessed(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException ex) {

        }
        return null;
    }

    public static BufferedImage createThumbnail(BufferedImage image) {
        image = resize(image, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        // Let's add a little border before we return result.
        return pad(image, 2);
    }

    public static BufferedImage createProcessed(BufferedImage image) {
        int Width = image.getWidth() - 1;
        image = resize(image, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(image, 4);
    }

    public LinkedList<ImageStore> getImagesForUser(String username) {
        LinkedList<ImageStore> images = new LinkedList<>();
        Session session = cluster.connect("instagrim");
        PreparedStatement ps = session.prepare("select postid from posts where user =?");
        BoundStatement bs = new BoundStatement(ps);
        ResultSet rs = session.execute(bs.bind(username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for (Row row : rs) {
                ImageStore image = new ImageStore();
                UUID uuid = row.getUUID("imageID");
                System.out.println("UUID" + uuid.toString());
                image.setID(uuid);
                images.add(image);

            }
        }
        return images;
    }

    public LinkedList<ImageStore> getMostRecent() {
        LinkedList<ImageStore> images = new LinkedList<>();
        Session session = cluster.connect("instagrim");
        SimpleStatement statement = new SimpleStatement("select imageid from images limit 9");
        ResultSet rs = session.execute(statement);

        if (rs.isExhausted()) {

        } else {
            for (Row row : rs) {
                ImageStore image = new ImageStore();
                UUID uuid = row.getUUID("imageid");
                image.setID(uuid);
                images.add(image);
            }
        }
        return images;
    }

    public ImageStore getImage(int imageType, java.util.UUID imageID) {
        Session session = cluster.connect("instagrim");
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        try {
            ResultSet rs = null;
            PreparedStatement ps = null;

            if (imageType == Convertors.DISPLAY_IMAGE) {

                ps = session.prepare("select image,imagelength,type from images where imageID =?");
            } else if (imageType == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from images where imageID =?");
            } else if (imageType == Convertors.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from images where imageID =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            imageID));

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    if (imageType == Convertors.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                    } else if (imageType == Convertors.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");

                    } else if (imageType == Convertors.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }

                    type = row.getString("type");

                }
            }
        } catch (Exception ex) {
            System.out.println("Can't get Image" + ex);
            return null;
        }
        session.close();
        ImageStore image = new ImageStore();
        image.setImage(bImage, length, type);

        return image;
    }

}
