package es.bsc.aeneas.cassandra.translator;

 
import es.bsc.aeneas.core.model.gen.CassandraClusterType;
import es.bsc.aeneas.core.model.gen.CrudType;
import es.bsc.aeneas.core.rosetta.ClusterHandler;
import es.bsc.aeneas.core.rosetta.Result;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import java.util.Collection;
import java.util.concurrent.Callable;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
 
public abstract class AbstractCassandraDB implements ClusterHandler {

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

    @Override
    public void init(CassandraClusterType clusterType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Callable<Result> query(CrudType ct, Collection<PathMatch> match) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
