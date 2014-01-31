/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.cassandrabm.nodeagent.repositories;

import es.bsc.cassandrabm.nodeagent.model.AEMetric;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.DynamicComposite;
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
public class MetricsRepository {

    private static final Logger log = Logger.getLogger(MetricsRepository.class.getName());
    private final int MAX_RESULTS_SIZE;
    private final Cluster cluster;
    private final Keyspace ksp;

    @Inject
    MetricsRepository(Configuration conf) {
        MAX_RESULTS_SIZE = conf.getInt("querycontroller.maxresultssize", 100000);
        String clusterlocation = conf.getString("aneas-recorder.clusterlocation");
        String clustername = conf.getString("aneas-recorder.clustername");
        log.log(Level.INFO, "Connectiong to the cluster {0} at address {1}", new Object[]{clustername, clusterlocation});
        cluster = HFactory.getOrCreateCluster(clustername, clusterlocation);
        ksp = HFactory.createKeyspace("metricspace", cluster);


    }

    public List<AEMetric> getMetrics(String context) {
        return getMetrics(context, null);
    }

    public List<AEMetric> getMetrics() {
        return getMetrics(null, null);
    }

    /**
     * *
     *
     * @param context {
     * @o}
     * @param from
     * @return a list of metrics
     *
     */
    public List<AEMetric> getMetrics(String context, String from) {

        DynamicComposite s;
        DynamicComposite t;
        if (context == null) {
            s = null;
            t = null;
        } else {
            if (from == null) {
                s = new DynamicComposite();
                s.addComponent(0, context, AbstractComposite.ComponentEquality.EQUAL);
            } else {
                s = new DynamicComposite(context, from);
            }
            t = new DynamicComposite();
            t.addComponent(0, context, AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);
        }
        List<AEMetric> lms = new ArrayList<AEMetric>(MAX_RESULTS_SIZE);
        QueryResult<ColumnSlice<DynamicComposite, byte[]>> q = HFactory.createSliceQuery(ksp, StringSerializer.get(),
                DynamicCompositeSerializer.get(), BytesArraySerializer.get()).setColumnFamily("lists").setKey("metrics-list")
                .setRange(s, t, false, MAX_RESULTS_SIZE).execute();
        List<HColumn<DynamicComposite, byte[]>> columns = q.get().getColumns();
        for (int i = 0; i < columns.size(); i++) {
            HColumn<DynamicComposite, byte[]> c = columns.get(i);
            String contextName = c.getName().get(0, StringSerializer.get());
            String metricname = c.getName().get(1, StringSerializer.get());
            lms.add(new AEMetric(contextName, metricname));
        }



        return lms;
    }
}
