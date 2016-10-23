package uk.ac.dundee.computing.tjn.instagrim.lib;

import com.datastax.driver.core.*;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author James Neill
 */
public final class CassandraHosts {

    private static Cluster cluster;
    static String Host = "127.0.0.1";  //at least one starting point to talk to

    /**
     *
     */
    public CassandraHosts() {

    }

    /**
     *
     * @return
     */
    public static String getHost() {
        return (Host);
    }

    /**
     *
     * @param cluster
     *
     * @return
     */
    public static String[] getHosts(Cluster cluster) {

        if (cluster == null) {
            System.out.println("Creating cluster connection");
            cluster = Cluster.builder().addContactPoint(Host).build();
        }
        System.out.println("Cluster Name " + cluster.getClusterName());
        Metadata mdata = null;
        try {
            mdata = cluster.getMetadata();
        } catch (Exception ex) {
            System.out.println("Can't get metadata");
            System.out.println("Exception " + ex);
            return (null);
        }
        Set<Host> hosts = mdata.getAllHosts();
        String sHosts[] = new String[hosts.size()];

        Iterator<Host> it = hosts.iterator();
        int i = 0;
        while (it.hasNext()) {
            Host ch = it.next();
            sHosts[i] = (String) ch.getAddress().toString();

            System.out.println("Hosts" + ch.getAddress().toString());
            i++;
        }

        return sHosts;
    }

    /**
     *
     * @return
     */
    public static Cluster getCluster() {
        System.out.println("getCluster");
        cluster = Cluster.builder()
                .addContactPoint(Host).build();
        if (getHosts(cluster) == null) {
            return null;
        }
        Keyspaces.SetUpKeySpaces(cluster);

        return cluster;

    }

    /**
     *
     */
    public void close() {
        cluster.close();
    }

}
