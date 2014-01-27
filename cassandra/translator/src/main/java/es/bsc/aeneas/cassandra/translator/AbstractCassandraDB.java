package es.bsc.aeneas.cassandra.translator;

import es.bsc.aeneas.core.model.gen.CassandraClusterType;
import static com.google.common.base.Preconditions.*;
import es.bsc.aeneas.cassandra.mapping.Cassa;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.rosetta.ClusterHandler;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import me.prettyprint.hector.api.Cluster;

public abstract class AbstractCassandraDB implements ClusterHandler {

    protected Cluster cluster = null;
    protected CassandraClusterType cclusterType = null;
    protected Cassa cassa;
 
    @Override
    public void close() {
        //TODO issues related on closing connections. 
        // cluster.getConnectionManager().shutdown(); /
    }

 
    

    @Override
    public void init(ClusterType clusterType) throws UnreachableClusterException {
        checkArgument(clusterType instanceof CassandraClusterType, "Call this init with a CassandraClusterType");
        cclusterType = (CassandraClusterType) clusterType;
        cassa=new Cassa(cclusterType);
      

    }
    
     

    
}
