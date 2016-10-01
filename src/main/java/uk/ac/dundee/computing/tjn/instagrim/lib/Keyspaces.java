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
            String createUserImageListTable = "CREATE TABLE if not exists instagrim.userimagelist (\n"
                    + "imageid uuid,\n"
                    + "user varchar,\n"
                    + "image_added timestamp,\n"
                    + "PRIMARY KEY (user,image_added)\n"
                    + ") WITH CLUSTERING ORDER BY (image_added desc);";
            String createAccountsTable = "CREATE TABLE if not exists instagrim.accounts (\n"
                    + "      username text PRIMARY KEY,\n"
                    + "      password text,\n"
                    + "      name text,\n"
                    + "      email text,\n"
                    + "      emailVerified boolean,\n"
                    + "      base32secret text,\n"
                    + "      bio text,\n"
                    + "      profile_pic blob\n"
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
            System.out.println("" + createUserImageListTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createUserImageListTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create user image list table " + ex);
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
