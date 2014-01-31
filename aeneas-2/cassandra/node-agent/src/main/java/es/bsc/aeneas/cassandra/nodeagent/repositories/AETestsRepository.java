/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.cassandra.nodeagent.repositories;

import com.beust.jcommander.internal.Lists;
import es.bsc.aeneas.core.nodeagent.model.AETest;
import es.bsc.aeneas.core.nodeagent.repositories.AETestRepo;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ccugnasc
 */
@Repository
public class AETestsRepository implements AETestRepo {

    private static final Logger log = Logger.getLogger(AETestsRepository.class.getName());
    private final int MAX_RESULTS_SIZE;
    private final Cluster cluster;
    private final Keyspace ksp;

    @Inject
    AETestsRepository(Configuration conf) {
        MAX_RESULTS_SIZE = conf.getInt("querycontroller.maxresultssize", 10000);
        String clusterlocation = conf.getString("aneas-recorder.clusterlocation");
        String clustername = conf.getString("aneas-recorder.clustername");
        log.log(Level.INFO, "Connectiong to the cluster {0} at address {1}", new Object[]{clustername, clusterlocation});
        cluster = HFactory.getOrCreateCluster(clustername, clusterlocation);
        ksp = HFactory.createKeyspace("metricspace", cluster);


    }

    @Override
    public List<AETest> getTests(String context, String from) throws Exception {
        if (context == null) {
            context = "workloader";
        }
        Composite l;
        if (from == null) {
            l = null;
        } else {
            l = new Composite();
            l.addComponent(0, from, AbstractComposite.ComponentEquality.EQUAL);
        }
        boolean cont;
        SortedMap<String, AETest> m = new TreeMap<String, AETest>();
        do {
            QueryResult<ColumnSlice<Composite, Long>> q = HFactory.createSliceQuery(ksp, StringSerializer.get(),
                    CompositeSerializer.get(), LongSerializer.get()).setColumnFamily("tests").setKey(context)
                    .setRange(l, null, false, MAX_RESULTS_SIZE).execute();

            List<HColumn<Composite, Long>> columns = q.get().getColumns();
            if (columns.size() == MAX_RESULTS_SIZE) {
                cont = true;
                l = columns.get(columns.size() - 1).getName();
            } else {
                cont = false;
            }
            for (int i = 0; i < columns.size(); i++) {
                HColumn<Composite, Long> c = columns.get(i);
                String test = c.getName().get(0, StringSerializer.get());
                AETest t;
                if (m.containsKey(test)) {
                    t = m.get(test);
                } else {
                    t = new AETest();
                    m.put(test, t);
                }
                String type = c.getName().get(2, StringSerializer.get());
                t.setName(test);
                Long d = LongSerializer.get().fromByteBuffer(c.getValueBytes());
                if (type.equals("start")) {
                    t.setStart(d);

                } else {
                    t.setStop(d);
                }
            }
        } while (cont);
        return Lists.newArrayList(m.values());
    }
}
