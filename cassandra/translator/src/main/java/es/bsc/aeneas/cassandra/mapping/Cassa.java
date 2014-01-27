/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.mapping;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import es.bsc.aeneas.cassandra.translator.Loader;
import es.bsc.aeneas.core.model.gen.CassandraClusterType;
import es.bsc.aeneas.core.model.gen.CassandraMatchType;
import es.bsc.aeneas.core.model.gen.CassandraSettings;
import es.bsc.aeneas.core.model.gen.ColumnFamilyType;
import es.bsc.aeneas.core.model.gen.KeyspaceType;
import es.bsc.aeneas.core.model.gen.KeyspaceType.ColumnFamilies;
import es.bsc.aeneas.core.model.gen.MatchType;
import es.bsc.aeneas.core.model.gen.StrategyType;
import es.bsc.aeneas.core.rosetta.exceptions.UnreachableClusterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author ccugnasc
 */
public class Cassa {

    @Inject
    Configuration conf;
    private List<Ks> keyspaces = new ArrayList();
    private final Map<String, Col> matches;
    public final Cluster cluster;

    public Cassa(CassandraClusterType cct) {
        try {
            cluster = Loader.getInstance().configureCluster(cct.getName(),
                    cct.getAddress());
        } catch (UnreachableClusterException ex) {
            throw new IllegalStateException("Impossible to init", ex);
        }
        Table<String, String, Reorder> reorder = HashBasedTable.create();
        List<MatchType> ms = cct.getMatches().getMatch();
        CassandraSettings settings = (CassandraSettings) cct.getSettings();
        for (KeyspaceType kt : settings.getKeyspaces().getKeyspace()) {
            for (ColumnFamilyType cf : kt.getColumnFamilies().getColumnFamily()) {
                Reorder r = new Reorder();
                r.keyspace = kt;
                r.columnFamily = cf;
                reorder.put(kt.getName(), cf.getName(), r);
            }
        }
        for (MatchType match : ms) {
            CassandraMatchType cmatch = (CassandraMatchType) match;
            String kname = cmatch.getKeyspaceName();
            String cfname = cmatch.getColumnFamilyName();
            if (!reorder.contains(kname, cfname)) {
                //I create fakes Keyspacetype or Columns  Types
                KeyspaceType kt = new KeyspaceType();
                kt.setName(kname);
                kt.setReplicationFactor(conf.getInt("cassandra.default.ks.replication-factor", 1));
                kt.setStrategy(StrategyType.valueOf(conf.getString("cassandra.default.ks.strategy", "SIMPLE_STRATEGY")));
                kt.setColumnFamilies(new ColumnFamilies());
                ColumnFamilyType cft = new ColumnFamilyType();
                cft.setName(cfname);
                Reorder r = new Reorder();
                r.keyspace = kt;
                r.columnFamily = cft;
                reorder.put(kt.getName(), cft.getName(), r);
            }
            reorder.get(kname, cfname).matches.add(cmatch);
        }
        ImmutableMap.Builder<String, Col> b = ImmutableMap.builder();
        for (String ksname : reorder.rowKeySet()) {
            Ks ks = new Ks(this,reorder.row(ksname));
            keyspaces.add(ks);
            for (CF cf : ks.cfs) {
                for (Col col : cf.columns) {
                    b.put(col.id, col);
                }
            }

        }
        matches = b.build();




    }

    public Col getMatch(String id) {
        return matches.get(id);
    }

    public List<KeyspaceDefinition> createKeyspaceDefinitions() {
        List<KeyspaceDefinition> list = new ArrayList();
        for (Ks k : keyspaces) {
            list.add(k.createKeyspaceDefinition());
        }
        return list;
    }
}
