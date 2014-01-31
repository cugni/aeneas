package es.bsc.aeneas.cassandra.translator;

import es.bsc.aeneas.core.model.gen.CassandraClusterType;
import static com.google.common.base.Preconditions.*;
import es.bsc.aeneas.cassandra.mapping.Cassa;
import es.bsc.aeneas.core.model.gen.ClusterType;
import es.bsc.aeneas.core.rosetta.ClusterHandler;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import javax.inject.Inject;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import org.apache.commons.configuration.Configuration;

public abstract class AbstractCassandraDB implements ClusterHandler {

    protected Cluster cluster = null;
    protected CassandraClusterType cclusterType = null;
    protected Cassa cassa;
    @Inject
    protected Configuration conf;
    private final boolean dropMetricspace = conf.getBoolean("dropMetricspace", false);

    
    @Override
    public void init(ClusterType clusterType) throws UnreachableClusterException {
        checkArgument(clusterType instanceof CassandraClusterType, "Call this init with a CassandraClusterType");
        cclusterType = (CassandraClusterType) clusterType;
        cassa = new Cassa(cclusterType);
        cassa.initHector();

        for (KeyspaceDefinition ks : cassa.createKeyspaceDefinitions()) {
            if (checkNotNull(cluster, "Is required to call the open method before the configure one")
                    .describeKeyspace(ks.getName()) == null) {
                cluster.addKeyspace(ks, true);

            }
            if (checkNotNull(cluster, "Is required to call the open method before the configure one")
                    .describeKeyspace(ks.getName()) == null) {
                //Add the schema to the cluster.
                //"true" as the second param means that Hector will block until all nodes see the change.
                cluster.addKeyspace(ks, true);
            } else if (dropMetricspace) {
                cluster.dropKeyspace(ks.getName(), true);
                cluster.addKeyspace(ks, true);

            }
        }
 
    }
     @Override
    public ClusterType getClusterType() {
        return cclusterType;
    }
}
