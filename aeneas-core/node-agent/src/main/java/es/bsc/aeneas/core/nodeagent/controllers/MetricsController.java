/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.bsc.aeneas.core.nodeagent.controllers;

import es.bsc.aeneas.core.nodeagent.model.AEMetric;
import es.bsc.aeneas.cassandra.nodeagent.repositories.MetricsRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import me.prettyprint.cassandra.model.HSlicePredicate;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DynamicCompositeSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.DynamicComposite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import org.apache.commons.configuration.Configuration;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author ccugnasc
 */
@Controller()
@RequestMapping("q/metrics")
public class MetricsController {

    private final Keyspace ksp;
    private static final Logger log = Logger.getLogger(MetricsController.class.getName());
    @Inject
    private MetricsRepository metRep;
    private final int MAX_NODES;

    @Inject
    MetricsController(Configuration conf) {
        MAX_NODES = conf.getInt("querycontroller.maxnodes", 200);
        String clusterlocation = conf.getString("aneas-recorder.clusterlocation");
        String clustername = conf.getString("aneas-recorder.clustername");
        log.log(Level.INFO, "Connectiong to the cluster {0} at address {1}", new Object[]{clustername, clusterlocation});
        Cluster cluster = HFactory.getOrCreateCluster(clustername, clusterlocation);
        ksp = HFactory.createKeyspace("metricspace", cluster);


    }

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public @ResponseBody
    List<AEMetric> getMetrics(
            @RequestParam(value = "context", required = false) String context,
            @RequestParam(value = "from", required = false) String from) throws Exception {
        return metRep.getMetrics(context, from);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/index-metrics")
    public @ResponseBody
    String indexMetrics() throws Exception {
        IndexerWorker iw = new IndexerWorker();
        iw.call();
        JSONObject jo = new JSONObject();
        boolean comp = iw.completed.get();
        jo.put("finished", comp);
        jo.put("start-time", iw.timeStart);
        jo.put("time-run", (comp ? iw.timeFinish : System.currentTimeMillis()) - iw.timeStart);
        return jo.toJSONString();
    }
    
    
//    @RequestMapping(method = RequestMethod.GET, value = "/index-metrics/{context}/{test}/{location}")
//    public @ResponseBody
//    String indexMetricsTest(
//            @PathVariable String context,
//             @PathVariable String test,
//              @PathVariable String location,
//            ) throws Exception {
//        
//        
//        return "{\"message\":\"success\"}";
//    }

    private class IndexerWorker implements Callable<String> {

        private final AtomicBoolean completed = new AtomicBoolean(false);
        private final AtomicInteger rowRead = new AtomicInteger(0);
        long timeStart;
        long timeFinish;

        @Override
        public String call() throws Exception {
            ThriftColumnFamilyTemplate<String, DynamicComposite> listsTemplate =
                    new ThriftColumnFamilyTemplate<String, DynamicComposite>(ksp,
                    "lists", StringSerializer.get(), new DynamicCompositeSerializer());
            ThriftColumnFamilyTemplate<String, Composite> testTemplate =
                    new ThriftColumnFamilyTemplate<String, Composite>(ksp,
                    "tests", StringSerializer.get(), new CompositeSerializer());

            QueryResult<ColumnSlice<DynamicComposite, byte[]>> execute =
                    HFactory
                    .createSliceQuery(ksp, StringSerializer.get(),
                    DynamicCompositeSerializer.get(),
                    BytesArraySerializer.get())
                    .setColumnFamily("lists")
                    .setKey("nodes-list")
                    .setRange(null, null, false, MAX_NODES).execute();
            Set<String> contexts = new HashSet<String>();
            for (HColumn<DynamicComposite, byte[]> columns : execute.get().getColumns()) {
                contexts.add(columns.getName().get(0, StringSerializer.get()));
            }

            /**
             * Adding the list of contexts
             */
            ColumnFamilyUpdater<String, DynamicComposite> updater =
                    listsTemplate.createUpdater("contexts-list");
            for (String context : contexts) {
                updater.setByteArray(new DynamicComposite(context), new byte[0]);
            }
            listsTemplate.update(updater);

            /**
             * Now for each context, I look for a test run on a node and I take
             * the fist for each context. The idea is that those tests have all
             * the metrics.
             */
            List<Composite> keys = new ArrayList<Composite>();
            for (String context : contexts) {
                Collection<Composite> columnNames = testTemplate.queryColumns(context,
                        new HSlicePredicate(CompositeSerializer.get()).setRange(null, null, false, 1)).getColumnNames();
                if (!columnNames.isEmpty()) {
                    Composite c = columnNames.iterator().next();
                    keys.add(new Composite(context,
                            c.get(0, StringSerializer.get()), //testname
                            c.get(1, StringSerializer.get()))); //location

                }

            }

            for (Composite key : keys) {
                String context = key.get(0, StringSerializer.get());
                Set<String> metrics = new HashSet<String>();
                List<HColumn<Composite, DynamicComposite>> columns;
                Composite start = null;
                do {

                    columns =
                            HFactory.createSliceQuery(ksp,
                            CompositeSerializer.get(),
                            CompositeSerializer.get(),
                            DynamicCompositeSerializer.get())
                            .setColumnFamily("metrics")
                            .setKey(key)
                            .setRange(start, null, false, 1000)
                            .execute().get().getColumns();


                    for (HColumn<Composite, DynamicComposite> c : columns) {
                        metrics.add(c.getName().get(0, StringSerializer.get()));
                    }
                    start = columns.get(columns.size() - 1).getName();
                    rowRead.getAndAdd(columns.size());


                } while (columns.size() > 1);

                ColumnFamilyUpdater<String, DynamicComposite> up = listsTemplate.createUpdater("metrics-list");
                for (String m : metrics) {
                    up.setByteArray(new DynamicComposite(context, m), new byte[0]);
                }
                listsTemplate.update(up);
            }

            return "completed";
        }
    }
}
