/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.mapping;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import es.bsc.aeneas.core.model.gen.CassandraClusterType;
import es.bsc.aeneas.core.model.gen.CassandraMatchType;
import es.bsc.aeneas.core.model.gen.CassandraSettings;
import es.bsc.aeneas.core.model.gen.ColumnFamilyType;
import es.bsc.aeneas.core.model.gen.KeyspaceType;
import es.bsc.aeneas.core.model.gen.KeyspaceType.ColumnFamilies;
import es.bsc.aeneas.core.model.gen.MatchType;
import es.bsc.aeneas.core.model.gen.StrategyType;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author ccugnasc
 */
public class Cassa {

    @Inject
    Configuration conf;
    private List<Ks> keyspaces=new ArrayList();

    public Cassa(CassandraClusterType cct) {
        Table<String, String, Reorder> reorder = HashBasedTable.create();
        List<MatchType> matches = cct.getMatches().getMatch();
        CassandraSettings settings = (CassandraSettings) cct.getSettings();
        for (KeyspaceType kt : settings.getKeyspaces().getKeyspace()) {
            for (ColumnFamilyType cf : kt.getColumnFamilies().getColumnFamily()) {
                Reorder r = new Reorder();
                r.keyspace = kt;
                r.columnFamily = cf;
                reorder.put(kt.getName(), cf.getName(), r);
            }
        }
        for (MatchType match : matches) {
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
        for(String ksname:reorder.rowKeySet()){
            
            keyspaces.add(new Ks(reorder.row(ksname)));
        }


    }

    
}
