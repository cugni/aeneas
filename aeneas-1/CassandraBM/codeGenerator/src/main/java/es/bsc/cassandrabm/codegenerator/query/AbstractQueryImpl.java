/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.codegenerator.query;

import es.bsc.cassandrabm.commons.CUtils;
import me.prettyprint.cassandra.connection.HClientPool;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * @author cesare
 */
public abstract class AbstractQueryImpl implements QueryInterface {

    protected static final Logger log = Logger.getLogger(AbstractQueryImpl.class.getName());
    protected final Keyspace keyspace;
    public final static String clusterName = CUtils.getString("clustername", "CassandraBM");
    public final static String location = CUtils.getString("clusterlocation", "localhost:9160");
    
    public AbstractQueryImpl(String keyspaceName) {

        Cluster cluster;
        try {
            log.log(Level.INFO, "connecting to the cluster {0} at {1}", new Object[]{clusterName, location});
            cluster = HFactory.getOrCreateCluster(clusterName, location);
            String nodes = CUtils.getString("nodes");
            if (nodes != null) {
                StringTokenizer tok = new StringTokenizer(nodes, ",");
                while (tok.hasMoreTokens()) {
                    String node = tok.nextToken();
                    cluster.getConnectionManager().addCassandraHost(new CassandraHost(node));
                    log.log(Level.INFO, "Adding the cassandra node {0}", node);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for(CassandraHost hc:cluster.getConnectionManager().getHosts()){
             log.log(Level.INFO, "{0}:{1}", 
                     new Object[]{hc.getIp(), hc.getPort()});
        }
        if (cluster.getConnectionManager().getActivePools().isEmpty()) {
            throw new IllegalStateException("Cluster unreacheable ");
        }
        Collection<HClientPool> activePools = cluster.getConnectionManager().getActivePools();
        log.log(Level.INFO, "Active nodes in the pool {0}",activePools.size());
        for(HClientPool c:activePools){
            log.log(Level.INFO,"Connected to {0}",c.getCassandraHost().getIp());
        }
        this.keyspace = checkNotNull(HFactory.createKeyspace(keyspaceName, cluster), "Impossible get the keyspace ");
    }
}
