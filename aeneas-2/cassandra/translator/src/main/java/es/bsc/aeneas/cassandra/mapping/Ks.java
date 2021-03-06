package es.bsc.aeneas.cassandra.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import es.bsc.aeneas.cassandra.serializers.Serializers;
import es.bsc.aeneas.commons.CUtils;
import es.bsc.aeneas.core.model.gen.KeyspaceType;
import es.bsc.aeneas.core.model.gen.StrategyType;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;

/**
 *
 * @author cesare
 */
public class Ks {

    private static final Logger log = Logger.getLogger(Ks.class.getName());
    public final ImmutableList<CF> cfs;
    final KeyspaceType keyspaceType;
    public final String name;
    public final Cassa cassa;
    public final Keyspace ksp;
    //Consistency settings
    private final HConsistencyLevel readconsistency =
            HConsistencyLevel.valueOf(
            CUtils.getString("cassandra.readconsistency", HConsistencyLevel.QUORUM.name()));
    private final HConsistencyLevel writeconsistency =
            HConsistencyLevel.valueOf(CUtils.getString("cassandra.writeconsistency", HConsistencyLevel.ANY.name()));
    private final boolean autoDiscoverHosts = CUtils.getBoolean("cassandra.autoDiscoverHosts", true);

    Ks(Cassa cassa, Map<String, Reorder> row) {
        this.cassa = cassa;
        Reorder r = row.values().iterator().next();
        this.keyspaceType = r.keyspace;

        name = this.keyspaceType.getName();
        Builder<CF> builder = ImmutableList.builder();
        for (Reorder cft : row.values()) {
            builder.add(new CF(this, cft));
        }
        cfs = builder.build();
        //Add the schema to the cluster.
        //"true" as the second param means that Hector will block until all nodes see the change.
        ConfigurableConsistencyLevel configurableConsistencyLevel = new ConfigurableConsistencyLevel();
        Map<String, HConsistencyLevel> clread = new HashMap<String, HConsistencyLevel>(4);
        Map<String, HConsistencyLevel> clwrite = new HashMap<String, HConsistencyLevel>(4);

        for (ColumnFamilyDefinition cfd : createKeyspaceDefinition().getCfDefs()) {
            clread.put(cfd.getName(), readconsistency);
            clwrite.put(cfd.getName(), writeconsistency);
        }
        configurableConsistencyLevel.setReadCfConsistencyLevels(clread);
        configurableConsistencyLevel.setWriteCfConsistencyLevels(clwrite);

        CassandraHostConfigurator conf = new CassandraHostConfigurator();
        conf.setAutoDiscoverHosts(autoDiscoverHosts);

        ksp = HFactory.createKeyspace(createKeyspaceDefinition().getName(),
                cassa.cluster, configurableConsistencyLevel);


    }

    public KeyspaceDefinition createKeyspaceDefinition() {

        List<ColumnFamilyDefinition> cfDefs = new ArrayList<ColumnFamilyDefinition>(cfs.size());
        for (CF cft : cfs) {
            log.log(Level.INFO, "Creating Cassandra definition for Column family {0}", cft.name);
            Set<Class<? extends Serializer<?>>> valser = new HashSet<Class<? extends Serializer<?>>>(cft.columns.size());
            for (Col ct : cft.columns) {

                valser.add((Class<? extends Serializer<?>>) ct.getValueSerializer().getClass());
            }
            //setting the column family key validator		
            ColumnFamilyDefinition cfd = cft.createColumnFamilyDefinition(this.name);
            if (valser.size() == 1) {
                Class<? extends Serializer<?>> next = valser.iterator().next();
                Serializer<?> s = Serializers.getSerializerBySerializerClass(next);
                ComparatorType comparatorType = s.getComparatorType();
                String defVC;
                if (comparatorType.equals(ComparatorType.DYNAMICCOMPOSITETYPE)) {
                    defVC = ComparatorType.DYNAMICCOMPOSITETYPE.getClassName().concat(DynamicComposite.DEFAULT_DYNAMIC_COMPOSITE_ALIASES);
                } else {
                    defVC = comparatorType.getClassName();
                }
                cfd.setDefaultValidationClass(defVC);
            }
            cfDefs.addAll(Arrays.asList(cfd));


        }
        return HFactory.createKeyspaceDefinition(name,
                "org.apache.cassandra.locator." + getStrategy().value(),
                getReplicationFactor(), //replica factor 
                cfDefs);


    }

    public StrategyType getStrategy() {
        return keyspaceType.getStrategy();
    }

    public int getReplicationFactor() {
        return keyspaceType.getReplicationFactor();
    }
}
