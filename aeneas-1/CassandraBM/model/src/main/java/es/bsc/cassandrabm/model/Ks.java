package es.bsc.cassandrabm.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import es.bsc.cassandrabm.model.gen.ColumnFamilyType;
import es.bsc.cassandrabm.model.gen.KeyspaceType;
import es.bsc.cassandrabm.model.gen.StrategyType;
import es.bsc.cassandrabm.model.util.Serializers;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cesare
 */
public class Ks {

    private static final Logger log = Logger.getLogger(Ks.class.getName());
    public final ImmutableList<CF> cfs;
    final KeyspaceType keyspaceType;
    public final String name;

    public Ks(KeyspaceType keyspaceType) {
        this.keyspaceType = keyspaceType;
        name = keyspaceType.getName();
        Builder<CF> builder = ImmutableList.builder();
        for (ColumnFamilyType cft : keyspaceType.getColumnFamily()) {
            builder.add(new CF(cft));
        }
        cfs = builder.build();

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
