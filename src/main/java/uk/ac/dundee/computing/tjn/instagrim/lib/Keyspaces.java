package uk.ac.dundee.computing.tjn.instagrim.lib;

import com.datastax.driver.core.*;

public final class Keyspaces {

    public Keyspaces() {

    }

    public static void SetUpKeySpaces(Cluster c) {
        try {
            //Add some keyspaces here
            String createKeyspace = "create keyspace if not exists instagrim  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String createPostsTable = "CREATE TABLE if not exists instagrim.posts ("
                    + " postid uuid, "
                    + " username varchar,"
                    + " posted timestamp,"
                    + " caption varchar,"
                    + " likes set<varchar>,"
                    + " comments set<uuid>,"
                    + " image blob,"
                    + " processed blob,"
                    + " thumbnail blob,"
                    + " imagelength int,"
                    + " thumbnaillength int,"
                    + " processedlength int,"
                    + " type  varchar,"
                    + " name  varchar,"
                    + " PRIMARY KEY (postid, username)"
                    + ")";
            String createAccountPostsTable = "CREATE TABLE if not exists instagrim.accountposts (\n"
                    + "postid uuid,\n"
                    + "username varchar,\n"
                    + "posted timestamp,\n"
                    + "PRIMARY KEY (username, posted)\n"
                    + ") WITH CLUSTERING ORDER BY (posted desc);";
            String createCommentsTable = "CREATE TABLE if not exists instagrim.postcomments ("
                    + "commentid uuid,"
                    + "username varchar,"
                    + "posted timestamp,"
                    + "caption text,"
                    + "PRIMARY KEY (commentid)"
                    + ")";
//            String createPostsTable = "CREATE TABLE if not exists instagrim.posts (\n"
//                    + "postid uuid PRIMARY KEY,\n"
//                    + "username varchar,\n"
//                    + "posted timestamp,\n"
//                    + "caption text,\n"
//                    + "likes set<varchar>,\n"
//                    + "comments set<uuid>\n"
//                    + ");";
            String createAccountsTable = "CREATE TABLE if not exists instagrim.accounts (\n"
                    + "      username varchar PRIMARY KEY,\n"
                    + "      password text,\n"
                    + "      first_name varchar,\n"
                    + "      last_name varchar,\n"
                    + "      email varchar,\n"
                    + "      emailVerified boolean,\n"
                    + "      base32secret varchar,\n"
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
            System.out.println("" + createPostsTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createPostsTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create images table " + ex);
            }
            System.out.println("" + createAccountPostsTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(createAccountPostsTable);
                session.execute(cqlQuery);
            } catch (Exception ex) {
                System.out.println("Can't create account posts table " + ex);
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
