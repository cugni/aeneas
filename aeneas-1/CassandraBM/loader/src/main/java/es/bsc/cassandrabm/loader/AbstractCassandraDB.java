package es.bsc.cassandrabm.loader;

import es.bsc.cassandrabm.loader.exceptions.UnreachableClusterException;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;

public abstract class AbstractCassandraDB implements DBSetter, DBGetter {

    protected Cluster cluster = null;

    @Override
    public void open(String clusterName, String location) throws UnreachableClusterException {
        cluster=Loader.getInstance().configureCluster(clusterName,location);
    }

    @Override
    public void close() {
        //TODO issues related on closing connections. 
        // cluster.getConnectionManager().shutdown(); /
    }

    public abstract KeyspaceDefinition getKeyspaceDefinition();
}
