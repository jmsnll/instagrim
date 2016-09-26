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
                    + " picid uuid, "
                    + " interaction_time timestamp,"
                    + " title varchar,"
                    + " image blob,"
                    + " thumb blob,"
                    + " processed blob,"
                    + " imagelength int,"
                    + " thumblength int,"
                    + "  processedlength int,"
                    + " type  varchar,"
                    + " name  varchar,"
                    + " PRIMARY KEY (picid)"
                    + ")";
            String createUserImagesListTable = "CREATE TABLE if not exists instagrim.userImagesList (\n"
                    + "picid uuid,\n"
                    + "user varchar,\n"
                    + "pic_added timestamp,\n"
                    + "PRIMARY KEY (user,pic_added)\n"
                    + ") WITH CLUSTERING ORDER BY (pic_added desc);";
            String createAddressTable = "CREATE TYPE if not exists instagrim.address (\n"
                    + "      street text,\n"
                    + "      city text,\n"
                    + "      zip int\n"
                    + "  );";
            String createAccountsTable = "CREATE TABLE if not exists instagrim.accounts (\n"
                    + "      username text PRIMARY KEY,\n"
                    + "      password text,\n"
                    + "      first_name text,\n"
                    + "      last_name text,\n"
                    + "      email set<text>,\n"
                    + "      base32secret text"
                    + "  );";
            Session session = c.connect();
            try {
                PreparedStatement statement = session
                        .prepare(createKeyspace);
                BoundStatement boundStatement = new BoundStatement(
                        statement);
                ResultSet rs = session
                        .execute(boundStatement);
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
            System.out.println("" + createUserImagesListTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createUserImagesListTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create user pic list table " + ex);
            }
            System.out.println("" + createAddressTable);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(createAddressTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create Address type " + ex);
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
            System.out.println("Other keyspace or coulm definition error" + ex);
        }
    }
}
