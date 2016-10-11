package uk.ac.dundee.computing.tjn.instagrim.lib;

import com.datastax.driver.core.*;

public final class Keyspaces {

    public Keyspaces() {

    }

    public static void SetUpKeySpaces(Cluster c) {
        try {
            //Add some keyspaces here
            String createKeyspace = "create keyspace if not exists instagrim  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String createImagesTable = "CREATE TABLE if not exists instagrim.images ("
                    + " user varchar,"
                    + " imageid uuid, "
                    + " interaction_time timestamp,"
                    + " title varchar,"
                    + " image blob,"
                    + " thumb blob,"
                    + " processed blob,"
                    + " imagelength int,"
                    + " thumblength int,"
                    + " processedlength int,"
                    + " type  varchar,"
                    + " name  varchar,"
                    + " PRIMARY KEY (imageid)"
                    + ")";
            String createCommentsTable = "CREATE TABLE if not exists instagrim.postcomments ("
                    + "commentid uuid,"
                    + "user varchar,"
                    + "comment_posted timestamp,"
                    + "caption text,"
                    + "PRIMARY KEY (commentid)"
                    + ")";
            String createPostsTable = "CREATE TABLE if not exists instagrim.posts (\n"
                    + "imageid uuid,\n"
                    + "user varchar,\n"
                    + "image_added timestamp,\n"
                    + "caption text,\n"
                    + "likes set<text>,\n"
                    + "comments set<uuid>,\n"
                    + "PRIMARY KEY (user,image_added)\n"
                    + ") WITH CLUSTERING ORDER BY (image_added desc);";
            String createAccountsTable = "CREATE TABLE if not exists instagrim.accounts (\n"
                    + "      username text PRIMARY KEY,\n"
                    + "      password text,\n"
                    + "      first_name text,\n"
                    + "      last_name text,\n"
                    + "      email text,\n"
                    + "      emailVerified boolean,\n"
                    + "      base32secret text,\n"
                    + "      bio text,\n"
                    + "      profile_pic uuid\n"
                    + "  );";
            Session session = c.connect();
            try {
                PreparedStatement statement = session.prepare(createKeyspace);
                BoundStatement boundStatement = new BoundStatement(statement);
                ResultSet rs = session.execute(boundStatement);
                System.out.println("created instagrim ");
            } catch (Exception ex) {
                System.out.println("Can't create instagrim " + ex);
            }

            //now add some column families
            System.out.println("" + createImagesTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createImagesTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create tweet table " + ex);
            }
            System.out.println("" + createPostsTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createPostsTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create user image list table " + ex);
            }
            System.out.println("" + createCommentsTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createCommentsTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create post comments table " + ex);
            }
            System.out.println("" + createAccountsTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createAccountsTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create Address Profile " + ex);
            }
            session.close();

        } catch (Exception ex) {
            System.out.println("Other keyspace or coulmn definition error" + ex);
        }

    }
}
